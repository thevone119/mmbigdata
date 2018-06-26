/**
 * Created by Administrator on 2017/7/31.
 */
(function () {
    let myProductSearchModel = {
        template: `
        <div :style="bgClass">
          <Row>
                <i-col span="5">
                    <div style="border-bottom: 1px solid #eee">
                        <i-button type="text" icon="chevron-left" @click="close">返回</i-button>
                    </div>
                    <Tree ref="myTree" :data="treeData" :multiple="false" :show-checkbox="false"
                          @on-select-change="onSelTreeNode"></Tree>
                </i-col>
                <i-col span="19">
                    <div class="g-dg-search-banner">
                        <i-form ref="mySearchForm" :model="searchForm" :label-width="80">
                            <Row>
                        <i-col span="8">
                            <Form-item label="产品代码">
                                <i-input v-model="searchForm.code" placeholder="请输入"></i-input>
                            </Form-item>
                        </i-col>
                        <i-col span="8">
                            <Form-item label="产品名称">
                                <i-input v-model="searchForm.name" placeholder="请输入"></i-input>
                            </Form-item>
                        </i-col>
                        <i-col span="8">
                            <Form-item label="规格型号">
                                <i-input v-model="searchForm.sfModel" placeholder="请输入"></i-input>
                            </Form-item>
                        </i-col>
                    </Row>
                        </i-form>
                    </div>
                    <div class="g-dg-toolbar">
                        <i-button type="info" icon="search" @click="find">查询</i-button>
                    </div>
                    <i-table :data="dtData" :columns="dtCols" :height="getTableHeight" size="small" :highlight-row="true"
                             @on-selection-change="onSelRows" @on-current-change="onCurrentChange" @on-row-dblclick="selectItem"
                             stripe></i-table>
                    
                </i-col>
            </Row>
        </div>
        `,
        data() {
            return {
                apis: {
                    getProductTypeListForUser () {
                        return PB.apiRoute('/api/scm/product_type/getTreeForUser', 'GET');
                    },
                    getProductList(p_body) {
                        return PB.apiRoute('/api/scm/product/getList', 'GET', p_body);
                    },
                    findForUser(p_body) {
                        return PB.apiRoute('/api/scm/product/findForUser', 'GET', p_body);
                    }
                },
                bgClass: {
                    backgroundColor: '#fff',
                    display: 'none',
                    height: '100%',
                    width: '100%',
                    position: 'fixed',
                    top: 0,
                    zIndex: 1001, // 为了遮挡遮罩层的1000值
                    transform: 'translate3d(100%, 0, 0)',
                    '-webkit-transition': 'all .2s'
                },
                searchForm: {},
                treeData: [],
                treeSelNode: null,
                dtData: [],
                dtCols: [
//                    {
//                        type: 'selection',
//                        width: 60,
//                        align: 'center'
//                    },
                    {
                        title: '编码',
                        key: 'code'
                    },
                    {
                        title: '名称',
                        key: 'name'
                    },
                    {
                        title: '规格型号',
                        key: 'sfModel'
                    },
                    {
                        title: '操作',
                        fixed: 'right',
                        width: 120,
                        render: (h, params) => {
                            return h('div', [
                                h('Button', {
                                    props: {
                                        type: 'primary',
                                        size: 'small'
                                    },
                                    on: {
                                        click: () => {
                                            setTimeout(() => { // 延迟一下，让选中行事件先执行，才能拿到currentRow
                                                this.selectItem(this.currentRow)
                                            }, 1);
                                        }
                                    },
                                }, '选择')
                            ]);
                        }
                    }
                ],
                currentRow: null,
                selRows: null
            }
        },
        created(){
            this.loadTreeData();
        },
        computed: {
            getTableHeight(){
                return (window.innerHeight - 130);
            }
        },
        methods: {
            loadTreeData(){
                let that = this;
                PB.ajax(this.apis.getProductTypeListForUser(), function (d) {
                    let arrNodes = createTree(d.data);
                    that.treeData = arrNodes;
                });

                function createTree(p_data) {
                    let arrNodes = [];
                    for (let info of p_data) {
                        let node = createTreeNode(info);
                        if (info.children) {
                            node.children = createTree(info.children);
                        }
                        arrNodes.push(node);
                    }

                    return arrNodes;
                }

                function createTreeNode(p_obj) {
                    let node = {};
                    node.title = p_obj.name;
                    node.id = p_obj.id;
                    node.expand = true;
                    return node;
                }
            },
            loadChildren () {
                let that = this;
                PB.ajax(this.apis.getProductList({productTypeId:this.treeSelNode.id}), function (d) {
                    that.dtData = d.data;
                });
            },
            find(){
                let that = this;
                PB.ajax(this.apis.findForUser(this.searchForm), function (d) {
                    that.dtData = d.data;
                });
            },
            onSelTreeNode(node){
                if (node) {
                    this.treeSelNode = node[0];
                    this.loadChildren();
                }

            },
            onCurrentChange(currentRow, oldCurrentRow){
                this.currentRow = currentRow;
            },
            onSelRows(selection){
                this.selRows = selection;
            },
            selectItem(row){
                this.$emit('on-select-item', row);
                this.close();
                //PB.alert(row.name);
            },
            open(){
                this.bgClass.display = ''; // 不这样写会没过渡效果
                setTimeout(() => {
                    this.bgClass.transform = 'translate3d(0, 0, 0)';
                }, 1);
            },
            close(){
                this.bgClass.display = 'none'; // 关闭就不需要效果了
                this.bgClass.transform = 'translate3d(100%, 0, 0)';
            }
        }
    };

    window.myProductSearchModel = myProductSearchModel;
})();
