<template>
	<cl-crud ref="Crud">
		<cl-row>
			<!-- 刷新按钮 -->
			<cl-refresh-btn />
			<!-- 新增按钮 -->
			<cl-add-btn />
			<!-- 删除按钮 -->
			<cl-multi-delete-btn />
			<cl-flex1 />
			<!-- 条件搜索 -->
			<cl-search ref="Search" />
		</cl-row>

		<cl-row>
			<!-- 数据表格 -->
			<cl-table ref="Table">
				<template #column-key="{ scope }">
					<div style="display: flex; align-items: center; justify-content: center">
						<span>{{ scope.row.key }}</span>
						<div style="width: 20px"></div>
						<el-button
							size="small"
							:icon="DocumentCopy"
							circle
							type="primary"
							@click="handleCopy(scope.row.key)"
						/>
					</div>
				</template>
				<template #column-printerName="{ scope }">
					<div style="display: flex; align-items: center; justify-content: center">
						<span>{{ scope.row.printerName }}</span>
						<div style="width: 20px"></div>
						<el-button
							size="small"
							:icon="DocumentCopy"
							circle
							type="primary"
							@click="handleCopy(scope.row.printerName)"
						/>
					</div>
				</template>
				<template #column-payType="{ scope }">
					<span
						v-for="item in JSON.parse(scope.row.payType)"
						:key="item.value"
						style="margin-right: 20px"
					>
						<el-tag effect="dark" type="success" v-if="item == '1'">微信</el-tag>
						<el-tag effect="dark" type="primary" v-else>支付宝</el-tag>
					</span>
				</template>
				<template #column-defaultPayType="{ scope }">
					<span>
						<el-tag effect="dark" type="success" v-if="scope.row.defaultPayType == '1'"
							>微信</el-tag
						>
						<el-tag effect="dark" type="primary" v-else>支付宝</el-tag>
					</span>
				</template>

				<template #column-online="{ scope }">
					<span v-if="scope.row.online">
						<el-text class="mx-1" type="success">在线</el-text>
					</span>
					<span v-else>
						<el-text class="mx-1" type="danger">离线</el-text>
					</span>
				</template>

				<!-- <template #slot-sendBtn="{ scope }">
					<el-button v-if="scope.row.online" text bg @click="bingIns(scope.row)"
						>发送指令</el-button
					>
				</template> -->
				<template #slot-btn="{ scope }">
					<el-button text bg @click="bingLog(scope.row)">日志</el-button>
				</template>
				<template #slot-tel="{ scope }">
					<el-button text bg @click="bingTel(scope.row)">模版</el-button>
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

		<cl-dialog v-model="det.visible" :title="det.title" width="70%" height="500px">
			<el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
				<el-tab-pane label="设备日志" name="first">
					<machine-log ref="machineLogRef" :id="ids"></machine-log>
				</el-tab-pane>
				<el-tab-pane label="错误日志" name="second">
					<machine-log-err ref="machineLogErrRef" :id="ids"></machine-log-err>
				</el-tab-pane>
			</el-tabs>
		</cl-dialog>
		<cl-dialog v-model="detTel.visible" :title="detTel.title" width="70%" height="500px">
			<templateCon :telId="detTel.telId"></templateCon>
			<template #footer>
				<el-button @click="detTel.visible = false">关闭</el-button>
				<el-button type="success">确定</el-button>
			</template>
		</cl-dialog>
	</cl-crud>
</template>

<script lang="ts" setup>
defineOptions({
	name: 'kiosk-machine'
});
import { onMounted, reactive, ref } from 'vue';
import { useCrud, useTable, useUpsert, useSearch } from '@cool-vue/crud';
import { useCool } from '/@/cool';
import { useI18n } from 'vue-i18n';
import { useDict } from '/$/dict';
import { ElMessage } from 'element-plus';
import { DocumentCopy } from '@element-plus/icons-vue';
import MachineLog from './machineLog.vue';
import MachineLogErr from './machineErr.vue';
import type { TabsPaneContext } from 'element-plus';
import templateCon from './template.vue';
const { dict } = useDict();
const ids = ref();
const activeName = ref('first');
const { service } = useCool();
const machineLogRef = ref();
const machineLogErrRef = ref();
const { t } = useI18n();
const det = reactive({
	visible: false,
	visibleIns: false,
	title: '日志',
	titleIns: '发送指令'
});
const detTel = reactive({
	visible: false,
	visibleIns: false,
	title: '选择模版',
	titleIns: '发送指令',
	telId: 0
});
const payTypeOptions = dict.get('pay_type');
console.log(dict.get('pay_type').value);
// cl-upsert
const Upsert = useUpsert({
	items: [
		{
			label: t('设备名称'),
			prop: 'name',
			component: { name: 'el-input', props: { clearable: true } },
			span: 24
		},
		{
			label: t('负责人'),
			prop: 'principal',
			component: { name: 'el-input', props: { clearable: true } },
			span: 12
		},
		{
			label: t('联系电话'),
			prop: 'phone',
			component: { name: 'el-input', props: { clearable: true } },
			span: 12
		},
		{
			label: t('打印机名称'),
			prop: 'printerName',
			component: { name: 'el-input', props: { clearable: true } },
			span: 12
		},
		{
			label: t('打印机规格'),
			prop: 'printerSpec',
			component: { name: 'el-radio-group', options: dict.get('printer_spec') },
			span: 12
		},
		{
			label: '状态',
			prop: 'status',
			component: { name: 'cl-switch' },
			span: 12
		},
		{
			label: '重载配置',
			prop: 'reset',
			component: { name: 'cl-switch' },
			span: 12,
			hidden: ({ scope }) => {
				return !scope.id;
			}
		},
		{
			label: '激活状态',
			prop: 'activateStatus',
			component: { name: 'cl-switch', props: { disabled: true } },
			span: 12
		},
		{
			label: t('支付方式'),
			prop: 'payType',
			component: { name: 'el-checkbox-group', options: dict.get('pay_type') },
			hook: {
				bind(value, { form }) {
					if (value) return JSON.parse(value);
				},
				submit(value, { form }) {
					if (value) return JSON.stringify(value);
				}
			},
			span: 12
		},
		{
			label: t('默认支付方式'),
			prop: 'defaultPayType',
			component: { name: 'el-radio-group', options: dict.get('pay_type') },
			span: 12
		},
		{
			label: t('备注'),
			prop: 'remark',
			component: { name: 'el-input', props: { type: 'textarea', rows: 4 } },
			span: 24
		}
	]
});

// cl-table
const Table = useTable({
	columns: [
		{ type: 'selection' },
		{ label: t('设备名称'), prop: 'name', minWidth: 120 },
		{ label: '在线状态', prop: 'online', minWidth: 100 },
		{ label: t('设备密钥'), prop: 'key', minWidth: 200 },
		{ label: t('负责人'), prop: 'principal', minWidth: 120 },
		{ label: t('联系电话'), prop: 'phone', minWidth: 120 },
		{ label: t('打印机名称'), prop: 'printerName', minWidth: 160 },
		{
			label: t('打印机规格'),
			prop: 'printerSpec',
			minWidth: 120,
			dict: dict.get('printer_spec')
		},
		{ label: '状态', prop: 'status', minWidth: 100, component: { name: 'cl-switch' } },
		{
			label: '激活状态',
			prop: 'activateStatus',
			minWidth: 100,
			component: { name: 'cl-switch', props: { disabled: true } }
		},
		{
			label: t('支付方式'),
			prop: 'payType',
			minWidth: 180,
			// dict: dict.get('pay_type'),
			formatter(row) {
				return JSON.parse(row.payType);
			}
		},
		{
			label: t('默认支付方式'),
			prop: 'defaultPayType',
			minWidth: 120
			// dict: dict.get('pay_type')
		},
		{
			label: t('激活时间'),
			prop: 'activateTime',
			minWidth: 170,
			sortable: 'custom',
			component: { name: 'cl-date-text' }
		},
		{ type: 'op', width: 280, buttons: ['slot-tel', 'slot-btn', 'edit'] }
	]
});

const handleClick = (tab: TabsPaneContext, event: Event) => {
	console.log(tab, event);
	// 根据当前激活的tab刷新对应组件
	if (tab.paneName === 'first') {
		// 刷新设备日志组件
		console.log(machineLogRef.value);
		machineLogRef.value?.Crud?.refresh();
	} else if (tab.paneName === 'second') {
		// 刷新错误日志组件
		console.log(machineLogErrRef.value?.Crud);
		machineLogErrRef.value?.Crud?.refresh();
	}
};
// cl-search
const Search = useSearch();

// cl-crud
const Crud = useCrud(
	{
		service: service.kiosk.machine
	},
	app => {
		app.refresh();
	}
);
function bingLog(row: any) {
	ids.value = row.id;
	det.visible = true;
}
function bingTel(row: any) {
	detTel.telId = row.id;
	detTel.visible = true;
}

function bingIns(row: any) {
	ids.value = row.id;
	det.visibleIns = true;
}
// 复制
function handleCopy(text: string) {
	navigator.clipboard
		.writeText(text)
		.then(() => {
			ElMessage.success('复制成功');
		})
		.catch(() => {
			ElMessage.error('复制失败，请重试');
		});
}
// 刷新
function refresh(params?: any) {
	Crud.value?.refresh(params);
}
</script>
