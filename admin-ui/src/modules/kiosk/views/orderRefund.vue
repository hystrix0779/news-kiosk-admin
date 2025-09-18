<template>
	<cl-crud ref="Crud">
		<cl-row>
			<!-- 刷新按钮 -->
			<cl-refresh-btn />
			<!-- 新增按钮 -->
			<!-- 删除按钮 -->
			<cl-multi-delete-btn />
			<cl-flex1 />
			<!-- 条件搜索 -->
			<cl-search ref="Search" />
		</cl-row>

		<cl-row>
			<!-- 数据表格 -->
			<cl-table ref="Table" />
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
	name: 'kiosk-order-refund'
});

import { useCrud, useTable, useUpsert, useSearch } from '@cool-vue/crud';
import { useCool } from '/@/cool';
import { useI18n } from 'vue-i18n';

const { service } = useCool();
const { t } = useI18n();

// cl-upsert
const Upsert = useUpsert({
	items: [
		{
			label: t('订单号'),
			prop: 'orderNum',
			component: { name: 'el-input', props: { clearable: true } },
			span: 12
		},
		{
			label: t('退款状态'),
			prop: 'refundStatus',
			component: { name: 'el-input', props: { clearable: true } },
			span: 12,
			required: true
		},
		{
			label: t('退款申请时间'),
			prop: 'refundApplyTime',
			component: {
				name: 'el-date-picker',
				props: { type: 'datetime', valueFormat: 'YYYY-MM-DD HH:mm:ss' }
			},
			span: 12
		},
		{
			label: t('退款成功时间'),
			prop: 'refundSuccessTime',
			component: {
				name: 'el-date-picker',
				props: { type: 'datetime', valueFormat: 'YYYY-MM-DD HH:mm:ss' }
			},
			span: 12
		},
		{
			label: t('备注'),
			prop: 'remark',
			component: { name: 'el-input', props: { clearable: true } },
			span: 12
		},
		{
			label: t('退款原因'),
			prop: 'refundReason',
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
		{ label: t('订单号'), prop: 'orderNum', minWidth: 120 },
		{ label: t('退款状态'), prop: 'refundStatus', minWidth: 120 },
		{
			label: t('退款申请时间'),
			prop: 'refundApplyTime',
			minWidth: 170,
			sortable: 'custom',
			component: { name: 'cl-date-text' }
		},
		{
			label: t('退款成功时间'),
			prop: 'refundSuccessTime',
			minWidth: 170,
			sortable: 'custom',
			component: { name: 'cl-date-text' }
		},
		{ label: t('备注'), prop: 'remark', minWidth: 120 },
		{ label: t('退款原因'), prop: 'refundReason', minWidth: 120 },
		{ label: t('租户id'), prop: 'tenantId', minWidth: 120 },
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
		service: service.kiosk.orderRefund
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
