<template>
	<cl-crud ref="Crud">
		<cl-row>
			<!-- 刷新按钮 -->
			<cl-refresh-btn />
			<!-- 新增按钮 -->
			<cl-add-btn v-if="!props.telId" />
			<!-- 删除按钮 -->
			<cl-multi-delete-btn v-if="!props.telId" />
			<cl-flex1 />
			<!-- 条件搜索 -->
			<cl-search ref="Search" />
		</cl-row>

		<cl-row>
			<!-- 数据表格 -->
			<!-- <cl-table ref="Table" /> -->
			<!-- 文件区域 -->
			<el-scrollbar v-loading="loading" class="cl-upload-space-inner__file">
				<!-- 文件列表 -->
				<template v-if="listArr.length > 0">
					<div
						class="cl-upload-space-inner__file-list"
						:class="{
							'is-mini': browser.isMini
						}"
					>
						<div
							v-for="item in listArr"
							:key="item.preload || item.url"
							class="cl-upload-space-inner__file-item"
							@mouseenter="(hoverItem as any) = item"
							@mouseleave="hoverItem = null"
						>
							<el-checkbox
								:model-value="selectedIds.some(i => i.id === item.id)"
								@change="
									(val: string | number | boolean) =>
										handleSelect(item, Boolean(val))
								"
								class="template-checkbox"
							/>
							<cl-image
								:preview="false"
								:src="item.pic"
								fit="contain"
								:size="[240, 200]"
							/>
							<!-- 模版名称 -->
							<div class="title">{{ item.name }}</div>
							<!-- v-show="hoverItem === item" -->

							<div
								v-show="hoverItem === item"
								class="cl-upload-space-inner__file-index"
								@click.stop="
									handleSelect(item, !selectedIds.some(i => i.id === item.id))
								"
							>
								<div class="action-btns">
									<el-icon @click="onView(item)">
										<zoom-in />
									</el-icon>
									<el-icon v-if="!props.telId" @click.stop="onEdit(item)">
										<edit />
									</el-icon>
									<el-icon v-if="!props.telId" @click.stop="onDelete(item)">
										<delete-filled />
									</el-icon>
								</div>
							</div>
						</div>
					</div>
				</template>

				<!-- 空态 -->
			</el-scrollbar>
			<div
				class="cl-upload-space-inner__footer cl-pagination"
				style="width: 100%; justify-content: end"
			>
				<el-pagination
					v-model:current-page="paginationS.page"
					:size="browser.isMini ? 'small' : 'default'"
					:total="paginationS.total"
					:default-page-size="paginationS.size"
					background
					layout="prev, pager, next"
					@current-change="refresh()"
				/>

				<span v-show="!browser.isMini" class="total">{{
					$t('共 {total} 条', { total: paginationS.total })
				}}</span>
			</div>
		</cl-row>

		<cl-row>
			<cl-flex1 />
			<!-- 分页控件 -->
			<!-- <cl-pagination /> -->
		</cl-row>

		<!-- 新增、编辑 -->
		<cl-upsert ref="Upsert" />
	</cl-crud>
</template>

<script lang="ts" setup>
defineOptions({
	name: 'kiosk-template'
});
import { provide, reactive, ref, watch } from 'vue';
import { useCrud, useTable, useUpsert, useSearch } from '@cool-vue/crud';
import { useCool } from '/@/cool';
import { useI18n } from 'vue-i18n';
import { ZoomIn, Edit, DeleteFilled } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import SparkMD5 from 'spark-md5';
import { number } from 'echarts';
const hoverItem = ref(null);
// 选中的模板ID列表
const selectedIds = ref<any[]>([]);

// 发送选中的ID到父组件
const emit = defineEmits(['update:selected']);
// 是否加载中
const loading = ref(false);
// 文件列表
const listArr = ref<Eps.SpaceInfoEntity[]>([]);
const { service, browser } = useCool();
const { t } = useI18n();
const props = defineProps({
	telId: Number
});
// 分页信息
const paginationS = reactive({
	page: 1,
	size: 20,
	total: 0
});
// cl-upsert
const Upsert = useUpsert({
	items: [
		{
			label: t('模板名称'),
			prop: 'name',
			component: { name: 'el-input', props: { clearable: true } },
			span: 24
		},
		{
			label: t('模板图片'),
			prop: 'pic',
			required: true,
			component: {
				name: 'cl-upload',
				props: {
					multiple: false,
					type: 'image',
					size: 200,
					beforeUpload: file => {
						const reader = new FileReader();
						reader.onload = e => {
							// @ts-ignore
							Upsert.value.form.pic = e.target.result;
							fileToMd5(file).then(md5 => {
								console.log(md5);
								Upsert.value.form.md5 = md5;
							});
						};
						reader.readAsDataURL(file);
						return true;
					}
					// beforeUpload(file, item, { next }) {
					// 	console.log(file, item);
					// 	fileToMd5(file).then(md5 => {
					// 		console.log(md5);
					// 		Upsert.value?.setForm('md5', md5);
					// 		return next();
					// 	});
					// }
				}
			},
			span: 24
		}
		// {
		// 	label: t('MD5'),
		// 	prop: 'md5',
		// 	component: { name: 'el-input', props: { clearable: true } },
		// 	span: 12
		// },
		// {
		// 	label: t('价格'),
		// 	prop: 'price',
		// 	component: { name: 'el-input', props: { clearable: true } },
		// 	span: 12
		// },
		// {
		// 	label: t('是否默认'),
		// 	prop: 'isDefault',
		// 	component: { name: 'el-input', props: { clearable: true } },
		// 	span: 12
		// }
	]
	// onSubmit(data, { done, next }) {
	// 	next({
	// 		...data,
	// 		id: props.telId
	// 	});
	// }
});

// cl-table
const Table = useTable({
	columns: [
		{ type: 'selection' },
		{ label: t('模板图片'), prop: 'pic', minWidth: 120 },
		{ label: t('模板名称'), prop: 'name', minWidth: 120 },
		{ label: t('MD5'), prop: 'md5', minWidth: 120 },
		{ label: t('价格'), prop: 'price', minWidth: 120 },
		{ label: t('是否默认'), prop: 'isDefault', minWidth: 120 },
		{
			label: t('创建时间'),
			prop: 'createTime',
			minWidth: 170,
			sortable: 'desc',
			component: { name: 'cl-date-text' }
		},
		{
			label: t('更新时间'),
			prop: 'updateTime',
			minWidth: 170,
			sortable: 'custom',
			component: { name: 'cl-date-text' }
		},
		{ type: 'op', buttons: ['edit', 'delete'] }
	]
});

// cl-search
const Search = useSearch();

// cl-crud
const Crud = useCrud(
	{
		service: service.kiosk.template,
		async onRefresh(params, { next, done, render }) {
			// 1 默认调用
			const { list, pagination } = await next(params);
			listArr.value = list;
			paginationS.page = pagination.page;
			paginationS.size = pagination.size;
			paginationS.total = pagination.total;
			// 渲染数据
			render(list, pagination);
		}
	},
	app => {
		app.refresh();
	}
);

// 处理选择变化
const handleSelect = (item: any, checked: boolean) => {
	if (checked) {
		// 添加完整对象（先检查是否已存在）
		if (!selectedIds.value.some(i => i.id === item.id)) {
			selectedIds.value.push(item);
		}
	} else {
		// 取消选中，按 id 移除
		const index = selectedIds.value.findIndex(i => i.id === item.id);
		if (index > -1) {
			selectedIds.value.splice(index, 1);
		}
	}

	console.log('当前选中对象', selectedIds.value);
	emit('update:selected', selectedIds.value);
};
function fileToMd5(file: File): Promise<string> {
	return new Promise((resolve, reject) => {
		const chunkSize = 2 * 1024 * 1024; // 分片大小，2MB
		const chunks = Math.ceil(file.size / chunkSize);
		let currentChunk = 0;
		const spark = new SparkMD5.ArrayBuffer();
		const reader = new FileReader();

		reader.onload = e => {
			if (e.target?.result) {
				spark.append(e.target.result as ArrayBuffer);
			}

			currentChunk++;

			if (currentChunk < chunks) {
				loadNext();
			} else {
				// 计算完成
				resolve(spark.end());
			}
		};

		reader.onerror = () => {
			reject(new Error('文件读取失败'));
		};

		function loadNext() {
			const start = currentChunk * chunkSize;
			const end = Math.min(file.size, start + chunkSize);
			reader.readAsArrayBuffer(file.slice(start, end));
		}

		loadNext();
	});
}
function onView(item) {
	console.log('查看', item);
	const data = {
		id: item.id
	};
	Upsert.value?.info(data);
}

function onEdit(item) {
	console.log('编辑', item);
	console.log(Upsert.value);
	const data = {
		id: item.id
	};
	Upsert.value?.edit(data);
}

function onDelete(item) {
	console.log('删除', item);
	ElMessageBox.alert('确定删除吗', '删除', {
		confirmButtonText: 'OK',
		callback: (action: string) => {}
	});
}
// 刷新
function refresh(params?: any) {
	Crud.value?.refresh(params);
}
</script>
<style scoped lang="scss">
.cl-upload-space-inner__file {
	width: 100%;
	height: calc(100% - 110px);
	padding: 0 10px;
	box-sizing: border-box;
	position: relative;
	padding-top: 30px;

	.cl-upload-space-inner__file-item {
		height: 0;
		min-height: 270px;
		min-width: 200px;
		width: calc(12.5% - 10px);
		margin: 0 20px 30px 0;
		position: relative;
		box-sizing: border-box;
		padding-top: initial;
		.title {
			font-size: 16px;
			font-weight: 700;
			color: #6c6868;
			text-align: center;
			margin-top: 10px;
		}
	}

	.cl-upload-space-inner__file-index {
		position: absolute;
		left: 0;
		top: 0;
		height: 100%;
		width: 100%;
		background-color: rgba(0, 0, 0, 0.4);
		z-index: 10; // 确保在 cl-image 上方
		border-radius: 5px;
		cursor: pointer;
		pointer-events: auto;
		.action-btns {
			width: 100%;
			height: 40px;
			display: flex;
			gap: 12px;
			position: absolute;
			justify-content: center;
			top: 50%;
			left: 50%;
			transform: translate(-50%, -50%);
			cursor: pointer;
			i {
				font-size: 22px;
				color: #fff;
				cursor: pointer;

				&:hover {
					color: var(--el-color-primary);
				}
			}
		}
		span {
			position: absolute;
			right: 5px;
			top: 5px;
			background-color: var(--el-color-success);
			color: #fff;
			display: inline-block;
			height: 20px;
			width: 20px;
			text-align: center;
			line-height: 20px;
			border-radius: 20px;
			font-size: 14px;
		}
	}

	.cl-upload-space-inner__file-list {
		display: flex;
		flex-wrap: wrap;

		.is-mini {
			.cl-upload-space-inner__file-item {
				width: calc(50% - 10px);
			}
		}
	}

	.cl-upload-space-inner__file-empty {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		margin: auto;
		height: 180px;
		width: 360px;
		max-width: 80%;
		border-radius: 6px;
		border: 1px dashed var(--el-border-color);
		box-sizing: border-box;
		cursor: pointer;

		.cl-upload-space-inner__file-empty:hover {
			border-color: var(--el-color-primary);
		}

		i {
			font-size: 67px;
			color: #c0c4cc;
		}

		p {
			font-size: 14px;
			margin-top: 15px;
			color: #999;
		}
	}
}
.cl-upload-space-inner__file-index {
	position: relative;

	.template-checkbox {
		position: absolute;
		top: 10px;
		right: 10px;
		color: #fff;
	}
}
:deep(.el-checkbox) {
	position: absolute;
	right: 3px;
	top: 0px;
	z-index: 99;
}
</style>
