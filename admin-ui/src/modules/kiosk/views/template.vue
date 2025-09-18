<template>
	<cl-crud ref="Crud">
		<cl-row>
			<!-- 刷新按钮 -->
			<cl-refresh-btn />
			<!-- 新增按钮 -->
			<!-- <cl-add-btn /> -->
			<el-button type="primary" @click="onAdd">新增</el-button>
			<!-- 删除按钮 -->
			<!-- <cl-multi-delete-btn /> -->
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
							<img
								v-if="item.isDefault == 1"
								class="moren"
								src="https://obenefits.oss-cn-beijing.aliyuncs.com/app%2Fbase%2F%E9%BB%98%E8%AE%A4_cbc71dc405aa4485a09f8c1d76eb0f8c.png"
								alt=""
							/>
							<cl-image
								:preview="false"
								:src="item.pic"
								fit="contain"
								:size="[240, 200]"
							/>
							<!-- 模版名称 -->
							<div class="title">
								{{ item.name }}
								<el-tag v-if="item.price" type="danger">￥{{ item.price }}</el-tag>
							</div>
							<!-- v-show="hoverItem === item" -->
							<div
								v-show="hoverItem === item"
								class="cl-upload-space-inner__file-index"
							>
								<div class="action-btns">
									<el-icon @click="onView(item)">
										<zoom-in />
									</el-icon>
									<el-icon @click.stop="onEdit(item)">
										<edit />
									</el-icon>
									<el-icon @click.stop="onDelete(item)">
										<delete-filled />
									</el-icon>
								</div>
							</div>
						</div>
					</div>
				</template>

				<!-- 空态 -->
				<div v-else class="cl-upload-space-inner__file-empty">
					<el-icon class="el-icon--upload">
						<upload-filled />
					</el-icon>
					<p>{{ $t('将文件拖到此处，或点击按钮上传') }}</p>
				</div>
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

		<cl-dialog v-model="det.visible" :title="det.title" width="70%" height="500px">
			<templateCon @update:selected="onSelectedChange" :telId="props.telId"></templateCon>
			<template #footer>
				<el-button @click="det.visible = false">关闭</el-button>
				<el-button type="success" @click="onSubmit">确定</el-button>
			</template>
		</cl-dialog>
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
import templateCon from './templatePublic.vue';
import { number } from 'echarts';
const hoverItem = ref(null);
// 是否加载中
const loading = ref(false);
// 文件列表
const listArr = ref<Eps.SpaceInfoEntity[]>([]);
const { service, browser } = useCool();
const { t } = useI18n();
const det = reactive({
	visible: false,
	visibleIns: false,
	title: '公共模版',
	titleIns: '发送指令'
});
const selectedIds = ref<any[]>([]);

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
		// {
		// 	prop: "parentId",
		// 	label: "上级节点",
		// 	hook: {
		// 		submit(value) {
		// 			return value || null;
		// 		}
		// 	},
		// 	component: {
		// 		name: "slot-parentId"
		// 	}
		// },
		// {
		// 	label: t('MD5'),
		// 	prop: 'md5',
		// 	component: { name: 'el-input', props: { clearable: true } },
		// 	span: 12
		// },
		{
			label: t('价格'),
			prop: 'price',
			component: { name: 'el-input', props: { clearable: true } },
			span: 24
		},
		{
			label: t('是否默认'),
			prop: 'isDefault',
			component: { name: 'cl-switch', props: { clearable: true } },
			span: 12
		}
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
			if (props.telId) {
				params.machineId = props.telId;
			}
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
function onSubmit() {
	console.log('选中的ID', selectedIds.value);
	if (selectedIds.value.length > 0) {
		selectedIds.value.forEach(item => {
			if (item) {
				item.parentId = item.id;
				item.id = null;
				item.machineId = props.telId;
			}
		});
	}
	service.kiosk.template.add(selectedIds.value).then(res => {
		ElMessage.success('添加成功');
		det.visible = false;
		refresh();
	});
}

function onSelectedChange(ids: number[]) {
	console.log('父组件收到选中的ID', ids);
	selectedIds.value = ids;
}
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

function onAdd(item) {
	det.visible = true;
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
		callback: (action: string) => {
			service.kiosk.template.delete({ ids: [item.id] }).then(res => {
				ElMessage.success('删除成功');
				refresh();
			});
		}
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
		min-height: 280px;
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
			display: flex;
			align-items: center;
			justify-content: space-around;
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
.moren {
	position: absolute;
	right: -4px;
	top: -4px;
	width: 50px;
	z-index: 116;
}
</style>
