webpackJsonp([13],{JyH4:function(e,t){},UKTt:function(e,t){},avNv:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n=a("Xxa5"),r=a.n(n),s=a("exGp"),i=a.n(s),o=a("RwfU"),c=a("Ynfa"),l=a("Er6Z"),u={name:"tableList",data:function(){return{rules:{new_name:[{required:!0,message:"请输入表名称",trigger:"blur"},{pattern:/^[0-9a-zA-Z_]+$/,message:"只支持英文、数字和下划线",trigger:"blur"}]},isVisible:!1,inputForm:{new_name:""},allAlign:null,tableData:[],queryForm:{query:""}}},created:function(){this.getTables("")},methods:{goToAdd:function(){window.open("/#/table/add","_blank")},goToEdit:function(){var e=this.$refs.xTable.getCurrentRecord(),t=this.$router.resolve({path:"/table/edit",query:{tb_name:e.tb_name,db_name:e.db_name}});window.open(t.href,"_blank")},getTables:function(e){var t=this;return i()(r.a.mark(function a(){var n;return r.a.wrap(function(a){for(;;)switch(a.prev=a.next){case 0:return a.prev=0,a.next=3,t.$http.get("/api/tb/search",{params:{query_name:e}});case 3:200!==(n=a.sent).data.code?Object(l.a)(n.data):t.tableData=n.data.data,a.next=10;break;case 7:a.prev=7,a.t0=a.catch(0),Object(o.a)(a.t0.message);case 10:case"end":return a.stop()}},a,t,[[0,7]])}))()},deleteSubmit:function(){var e=this;return i()(r.a.mark(function t(){var a;return r.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:if(!(a=e.$refs.xTable.getCurrentRecord())){t.next=10;break}return t.next=4,c.VXETable.modal.confirm("您确定要删除选中的数据吗?");case 4:if("confirm"!==t.sent){t.next=8;break}return t.next=8,e.deleteTable(a);case 8:t.next=12;break;case 10:return t.next=12,c.VXETable.modal.message({content:"请至少选择一条数据",status:"error"});case 12:case"end":return t.stop()}},t,e)}))()},deleteTable:function(e){var t=this;return i()(r.a.mark(function a(){var n;return r.a.wrap(function(a){for(;;)switch(a.prev=a.next){case 0:return a.prev=0,a.next=3,t.$http.delete("/api/tb/delete",{params:{db_name:e.db_name,tb_name:e.tb_name}});case 3:if(200===(n=a.sent).data.code){a.next=8;break}Object(l.a)(n.data),a.next=11;break;case 8:return t.$message.success(n.data.msg),a.next=11,t.getTables("");case 11:a.next=16;break;case 13:a.prev=13,a.t0=a.catch(0),Object(o.a)(a.t0.message);case 16:case"end":return a.stop()}},a,t,[[0,13]])}))()},onQuery:function(){this.getTables(this.queryForm.query)},renameTable:function(){if(0===this.row.length)return Object(o.a)("请先选择一行数据");this.isVisible=!0},onSubmit:function(){var e=this;return i()(r.a.mark(function t(){var a;return r.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.prev=0,t.next=3,e.$http.post("api/tb/rename",{db_name:e.row.db_name,tb_name:e.row.tb_name,new_name:e.inputForm.new_name});case 3:if(200===(a=t.sent).data.code){t.next=8;break}Object(l.a)(a.data),t.next=12;break;case 8:return e.$message.success(a.data.msg),e.isVisible=!1,t.next=12,e.getTables("");case 12:t.next=17;break;case 14:t.prev=14,t.t0=t.catch(0),Object(o.a)(t.t0.message);case 17:case"end":return t.stop()}},t,e,[[0,14]])}))()},dispatch:function(){this.$refs.ruleForm.resetFields(),this.isVisible=!1}}},m={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"tableBody"},[a("el-form",{staticStyle:{"text-align":"right"},attrs:{inline:!0},model:{value:e.queryForm,callback:function(t){e.queryForm=t},expression:"queryForm"}},[a("el-form-item",[a("el-input",{attrs:{clearable:!0,placeholder:"请输入表名称"},model:{value:e.queryForm.query,callback:function(t){e.$set(e.queryForm,"query",t)},expression:"queryForm.query"}})],1),e._v(" "),a("el-form-item",[a("el-button",{attrs:{type:"primary"},on:{click:e.onQuery}},[e._v("查询")])],1)],1),e._v(" "),a("vxe-toolbar",{attrs:{perfect:""},scopedSlots:e._u([{key:"buttons",fn:function(){return[a("div",{staticStyle:{"text-align":"left"}},[a("vxe-button",{attrs:{size:"mini",icon:"el-icon-plus",status:"success",content:"新增表"},on:{click:e.goToAdd}}),e._v(" "),a("vxe-button",{attrs:{size:"mini",icon:"el-icon-delete",status:"success",content:"删除表"},on:{click:e.deleteSubmit}}),e._v(" "),a("vxe-button",{attrs:{size:"mini",icon:"el-icon-edit",status:"success",content:"重命名"},on:{click:e.renameTable}}),e._v(" "),a("vxe-button",{attrs:{size:"mini",icon:"el-icon-edit",status:"success",content:"修改表结构"},on:{click:e.goToEdit}}),e._v(" "),a("vxe-button",{attrs:{size:"mini",content:"导出"},on:{click:function(t){return e.$refs.xTable.exportData()}}})],1)]},proxy:!0}])}),e._v(" "),a("vxe-table",{ref:"xTable",attrs:{border:"","print-config":{},"export-config":{},"show-header-overflow":"","show-overflow":"",align:e.allAlign,"row-config":{isCurrent:!0},data:e.tableData}},[a("vxe-column",{attrs:{field:"db_name",title:"所属数据库"}}),e._v(" "),a("vxe-column",{attrs:{field:"tb_name",title:"表名"}})],1),e._v(" "),a("el-dialog",{attrs:{"close-on-click-modal":!1,visible:e.isVisible},on:{close:e.dispatch,"update:visible":function(t){e.isVisible=t}}},[a("el-form",{ref:"ruleForm",attrs:{model:e.inputForm,rules:e.rules}},[a("el-form-item",{attrs:{label:"表名称",prop:"new_name"}},[a("el-input",{attrs:{placeholder:"表名称仅支持英文、下划线和数字"},model:{value:e.inputForm.new_name,callback:function(t){e.$set(e.inputForm,"new_name",t)},expression:"inputForm.new_name"}})],1),e._v(" "),a("el-form-item",[a("el-button",{attrs:{type:"primary"},on:{click:e.onSubmit}},[e._v("保存")]),e._v(" "),a("el-button",{on:{click:e.dispatch}},[e._v("取消")])],1)],1)],1)],1)},staticRenderFns:[]};var b={name:"tablePage",components:{TableList:a("VU/8")(u,m,!1,function(e){a("JyH4")},"data-v-5e0dad97",null).exports,pageHead:a("Neex").a}},d={render:function(){var e=this.$createElement,t=this._self._c||e;return t("div",[t("page-head",{staticClass:"nav"}),this._v(" "),t("el-container",[t("el-card",[t("table-list")],1)],1)],1)},staticRenderFns:[]};var f=a("VU/8")(b,d,!1,function(e){a("UKTt")},"data-v-108c8ee0",null);t.default=f.exports}});
//# sourceMappingURL=13.c83e3d774d5c9e4123d5.js.map