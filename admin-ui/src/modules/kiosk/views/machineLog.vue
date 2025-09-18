<template>
	<cl-crud ref="Crud">
		<cl-row>
			<!-- 刷新按钮 -->
			<!-- <cl-refresh-btn /> -->
			<!-- 新增按钮 -->
			<!-- <cl-add-btn /> -->
			<!-- 删除按钮 -->
			<!-- <cl-multi-delete-btn /> -->

			<!-- <cl-flex1 /> -->
			<!-- 关键字搜索 -->
			<!-- <cl-search-key placeholder="搜索关键字" /> -->
		</cl-row>

		<cl-row>
			<!-- 数据表格 -->
			<cl-table :autoHeight="false" ref="Table" />
		</cl-row>

		<cl-row>
			<!-- <cl-flex1 /> -->
			<!-- 分页控件 -->
			<!-- <cl-pagination /> -->
		</cl-row>

		<!-- 新增、编辑 -->
		<cl-upsert ref="Upsert" />
	</cl-crud>
</template>

<script lang="ts" name="print-machinelog" setup>
import { useCrud, useTable, useUpsert } from '@cool-vue/crud';
import { useCool } from '/@/cool';

const { service } = useCool();
const props = defineProps({
	id: {
		type: Number,
		default: null
	}
});
console.log(props.id);
// cl-upsert
const Upsert = useUpsert({
	items: [
		{
			label: '设备ID',
			prop: 'machineId',
			component: { name: 'el-input', props: { clearable: true } }
		},
		{
			label: '登入时间',
			prop: 'loginTime',
			component: { name: 'el-input', props: { clearable: true } }
		},
		{
			label: '登出时间',
			prop: 'logoutTime',
			component: { name: 'el-input', props: { clearable: true } }
		}
	]
});

// cl-table
const Table = useTable({
	columns: [
		{ label: '设备ID', prop: 'machineId', minWidth: 140 },
		{ label: '登入时间', prop: 'loginTime', minWidth: 140 },
		{ label: '登出时间', prop: 'logoutTime', minWidth: 140 }
		// { type: "op", buttons: ["edit", "delete"] }
	]
});

// cl-crud
const Crud = useCrud(
	{
		service: service.kiosk.machineLog,
		async onRefresh(params, { next, done, render }) {
			if (props.id) {
				params.id = props.id;
			}
			// 1 默认调用
			const { list, pagination } = await next(params);
			// 渲染数据
			render(list, pagination);
		}
	},
	app => {
		if (props.id) {
			app.refresh({
				id: props.id
			});
		} else {
			app.refresh();
		}
	}
);

// 刷新
function refresh(params?: any) {
	Crud.value?.refresh(params);
}
// 暴露Crud变量，使父组件可以访问
defineExpose({
	Crud
});
</script>
