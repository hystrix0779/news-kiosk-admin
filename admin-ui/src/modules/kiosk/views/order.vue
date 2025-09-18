<template>
	<cl-crud ref="Crud">
		<cl-row>
			<!-- 刷新按钮 -->
			<cl-refresh-btn />
			<!-- 新增按钮 -->
			<!-- <cl-add-btn /> -->
			<!-- 删除按钮 -->
			<cl-multi-delete-btn />

			<cl-flex1 />

			<!-- 条件搜索 -->
			<cl-search ref="Search" />
		</cl-row>
		<cl-row>
			<cl-filter label="打印设备">
				<cl-select
					prop="machineId"
					:options="options.dataType"
					:width="160"
					v-model="machineIds"
					@change="changeSelect"
					:placeholder="$t('机器')"
				/>
			</cl-filter>
			<cl-filter label="设备模版">
				<cl-select
					prop="templateId"
					:options="options.telList"
					:width="160"
					:placeholder="$t('模版')"
				/>
			</cl-filter>
			<cl-filter label="创建时间">
				<cl-date-picker
					prop="createTimeRange"
					type="datetimerange"
					range-separator="To"
					start-placeholder="Start date"
					end-placeholder="End date"
				>
				</cl-date-picker>
			</cl-filter>
			<cl-filter label="支付方式">
				<cl-select
					prop="payType"
					:options="dict.get('pay_type')"
					:width="160"
					:placeholder="$t('支付方式')"
				/>
			</cl-filter>
			<cl-filter label="订单状态">
				<cl-select
					prop="status"
					:options="dict.get('order_status')"
					:width="160"
					:placeholder="$t('订单状态')"
				/>
			</cl-filter>
		</cl-row>

		<cl-row>
			<!-- 数据表格 -->
			<cl-table ref="Table">
				<template #column-status="{ scope }">
					<span>
						<el-popover
							v-if="scope.row.status == '9'"
							placement="top-start"
							title="备注"
							:width="200"
							trigger="hover"
							:content="scope.row.remark"
						>
							<template #reference>
								<el-tag effect="dark" type="danger">打印失败</el-tag>
							</template>
						</el-popover>
						<el-tag effect="dark" type="danger" v-else-if="scope.row.status == '8'"
							>退款失败</el-tag
						>
						<el-tag effect="dark" type="danger" v-else-if="scope.row.status == '7'"
							>已关闭</el-tag
						>
						<el-tag effect="dark" type="primary" v-else-if="scope.row.status == '6'"
							>已退款</el-tag
						>
						<el-tag effect="dark" type="primary" v-else-if="scope.row.status == '5'"
							>退款中</el-tag
						>
						<el-tag effect="dark" type="success" v-else-if="scope.row.status == '4'"
							>交易完成</el-tag
						>
						<el-tag effect="dark" type="primary" v-else-if="scope.row.status == '1'"
							>待打印</el-tag
						>
						<el-tag effect="dark" type="primary" v-else-if="scope.row.status == '0'"
							>代付款</el-tag
						>
					</span>
				</template>
			</cl-table>
		</cl-row>

		<cl-row>
			<cl-flex1 />
			<!-- 分页控件 -->
			<cl-pagination />
		</cl-row>

		<!-- 新增、编辑 -->
		<cl-upsert ref="Upsert" />
	</cl-crud>
</template>

<script lang="ts" setup>
defineOptions({
	name: 'kiosk-order'
});
import { computed, reactive, ref } from 'vue';
import { useCrud, useTable, useUpsert, useSearch } from '@cool-vue/crud';
import { useCool } from '/@/cool';
import { useI18n } from 'vue-i18n';
import { useDict } from '/$/dict';
const { dict } = useDict();

const { service } = useCool();
const { t } = useI18n();
const machineIds = ref('');
const machineIdsCom = computed(() => {
	return machineIds.value;
});
// 选项
const options = reactive({
	dataType: [] as { label: string; value: number }[],
	telList: [] as { label: string; value: number }[]
});
getMachine();
// cl-upsert
const Upsert = useUpsert({
	items: [
		{
			label: t('标题'),
			prop: 'title',
			component: { name: 'el-input', props: { clearable: true } },
			span: 12
		},
		{
			label: t('支付方式'),
			prop: 'payType',
			component: { name: 'el-input', props: { clearable: true } },
			span: 12
		},
		{
			label: t('支付时间'),
			prop: 'payTime',
			component: {
				name: 'el-date-picker',
				props: { type: 'datetime', valueFormat: 'YYYY-MM-DD HH:mm:ss' }
			},
			span: 12
		},
		{
			label: t('订单号'),
			prop: 'orderNum',
			component: { name: 'el-input', props: { clearable: true } },
			span: 12
		},
		{
			label: t('状态'),
			prop: 'status',
			component: { name: 'el-input', props: { clearable: true } },
			span: 12,
			required: true
		},
		{
			label: t('价格'),
			prop: 'price',
			component: { name: 'el-input', props: { clearable: true } },
			span: 12
		},
		{
			label: t('备注'),
			prop: 'remark',
			component: { name: 'el-input', props: { clearable: true } },
			span: 12
		},
		{
			label: t('关闭备注'),
			prop: 'closeRemark',
			component: { name: 'el-input', props: { clearable: true } },
			span: 12
		},
		{
			label: t('选择租户'),
			prop: 'tenantId',
			component: { name: 'el-input', props: { clearable: true } },
			span: 12
		}
	]
});

// cl-table
const Table = useTable({
	columns: [
		{ type: 'selection' },
		{ label: t('标题'), prop: 'title', minWidth: 120 },
		{ label: '机器', prop: 'machineName', minWidth: 120 },
		{ label: '模版', prop: 'templateName', minWidth: 120 },
		{
			label: '图片',
			prop: 'pic',
			minWidth: 100,
			component: { name: 'cl-image', props: { size: 50 } }
		},
		{
			label: t('支付方式'),
			prop: 'payType',
			minWidth: 170,
			dict: dict.get('pay_type')
		},
		{
			label: t('支付时间'),
			prop: 'payTime',
			minWidth: 170,
			sortable: 'custom',
			component: { name: 'cl-date-text' }
		},
		{ label: t('订单号'), prop: 'orderNum', minWidth: 120 },
		{
			label: t('状态'),
			prop: 'status',
			minWidth: 120,
			dict: dict.get('order_status')
			// formatter(row) {
			// 	console.log(row.status, '1111');
			// 	let str = '';
			// 	if (row.status == '4') {
			// 		str = '失败';
			// 	}
			// 	return str;
			// }
		},
		{ label: t('价格'), prop: 'price', minWidth: 120 },
		// { label: t('备注'), prop: 'remark', minWidth: 120 },
		// { label: t('关闭备注'), prop: 'closeRemark', minWidth: 120 },
		// { label: t('租户id'), prop: 'tenantId', minWidth: 120 },
		{
			label: t('创建时间'),
			prop: 'createTime',
			minWidth: 170,
			sortable: 'desc',
			component: { name: 'cl-date-text' }
		},

		{ type: 'op', buttons: ['delete'] }
	]
});
const changeSelect = val => {
	const data = {
		machineId: machineIdsCom.value
	};
	service.kiosk.template.list(data).then(res => {
		console.log(res);
		res.forEach(e => {
			// 赋值
			options.telList.push({
				label: e.name || '-',
				value: e.id
			});
		});
	});
};
// 获取机器列表
function getMachine() {
	service.kiosk.machine.list().then(res => {
		console.log(res);
		res.forEach(e => {
			// 赋值
			options.dataType.push({
				label: e.name || '-',
				value: e.id
			});
		});
	});
}
// cl-search
const Search = useSearch();

// cl-crud
const Crud = useCrud(
	{
		service: service.kiosk.order,
		async onRefresh(params, { next, done, render }) {
			console.log(params, '22222');
			if (params.createTimeRangeEndTime || params.createTimeRangeStartTime) {
				params.createTimeRange =
					params.createTimeRangeStartTime + ',' + params.createTimeRangeEndTime;
			}
			const { list, pagination } = await next(params);
			// 渲染数据
			render(list, pagination);
		}
	},
	app => {
		app.refresh();
	}
);

// 刷新
function refresh(params?: any) {
	Crud.value?.refresh(params);
}
</script>
