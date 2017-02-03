webpackJsonp([0],{1019:function(module,exports,__webpack_require__){"use strict";var core_1=__webpack_require__(0),common_1=__webpack_require__(16),forms_1=__webpack_require__(14),error_handler_1=__webpack_require__(1024),nga_module_1=__webpack_require__(394),admin_component_1=__webpack_require__(1026),admin_service_1=__webpack_require__(1027),admin_routing_1=__webpack_require__(1036),ng2_bootstrap_1=__webpack_require__(580),AdminModule=function(){function AdminModule(){}return AdminModule=__decorate([core_1.NgModule({imports:[common_1.CommonModule,forms_1.FormsModule,forms_1.ReactiveFormsModule,nga_module_1.NgaModule,ng2_bootstrap_1.ModalModule,admin_routing_1.routing],declarations:[admin_component_1.AdminComponent],providers:[admin_service_1.AdminService,error_handler_1.ErrorHandler]}),__metadata("design:paramtypes",[])],AdminModule)}();Object.defineProperty(exports,"__esModule",{value:!0}),exports.default=AdminModule},1024:function(module,exports,__webpack_require__){"use strict";var core_1=__webpack_require__(0),ErrorHandler=function(){function ErrorHandler(){}return ErrorHandler.prototype.handleError=function(error){},ErrorHandler=__decorate([core_1.Injectable(),__metadata("design:paramtypes",[])],ErrorHandler)}();exports.ErrorHandler=ErrorHandler},1025:function(module,exports){"use strict";var Pagination=function(){function Pagination(total,currentPage){if(this.pages=[],this.totalPages=Math.ceil(total/10),this.totalPages<=5)this.startPage=1,this.endPage=this.totalPages;else{var c2=+currentPage+2;currentPage<=3?(this.startPage=1,this.endPage=5):c2>=this.totalPages?(this.startPage=this.totalPages-4,this.endPage=this.totalPages):(this.startPage=+currentPage-2,this.endPage=+currentPage+2)}for(var i=this.startPage;i<=this.endPage;i++)this.pages.push(i)}return Pagination}();exports.Pagination=Pagination},1026:function(module,exports,__webpack_require__){"use strict";var core_1=__webpack_require__(0),forms_1=__webpack_require__(14),modal_component_1=__webpack_require__(170),error_handler_1=__webpack_require__(1024),pagination_1=__webpack_require__(1025),admin_service_1=__webpack_require__(1027),admin_1=__webpack_require__(1037),AdminComponent=function(){function AdminComponent(adminService,errorHandler,fb){this.adminService=adminService,this.errorHandler=errorHandler,this.errorCreate=!1,this.createButton="Save",this.errorDelete=!1,this.deleteButton="Delete",this.loadingStatus=!0,this.adminService.checkLogin(),this.getAdmin(1),this.currentPage=1,this.formPage=fb.group({page:["",forms_1.Validators.compose([forms_1.Validators.required,forms_1.Validators.pattern("[1-9][0-9]*")])]}),this.formCreate=fb.group({usernameC:["",forms_1.Validators.compose([forms_1.Validators.required,forms_1.Validators.minLength(3),forms_1.Validators.maxLength(16)])],passwordC:["",forms_1.Validators.compose([forms_1.Validators.required,forms_1.Validators.minLength(3),forms_1.Validators.maxLength(255)])],namaC:["",forms_1.Validators.compose([forms_1.Validators.required,forms_1.Validators.minLength(2),forms_1.Validators.maxLength(128)])],roleC:[""]})}return AdminComponent.prototype.getAdmin=function(pageNumber){var _this=this;this.loadingStatus=!0,this.adminService.getAdmin(pageNumber).subscribe(function(admin){_this.admin=admin,_this.adminService.getCount().subscribe(function(count){_this.count=count,_this.currentPage=pageNumber,_this.formPage.setValue({page:pageNumber}),_this.pagination=new pagination_1.Pagination(_this.count,_this.currentPage),_this.loadingStatus=!1},_this.errorHandler.handleError)},this.errorHandler.handleError)},AdminComponent.prototype.loadPage=function(event){var target=event.target||event.srcElement||event.currentTarget;this.currentPage=target.innerText,this.getAdmin(this.currentPage)},AdminComponent.prototype.showCreateModal=function(){this.createAdminModal.show()},AdminComponent.prototype.hideCreateModal=function(){this.createAdminModal.hide()},AdminComponent.prototype.createAdmin=function(admin){var _this=this;this.loadingStatus=!0,this.createButton="Please wait";var c=new admin_1.Admin(null,admin.namaC,admin.usernameC,admin.passwordC);this.adminService.createAdmin(c).subscribe(function(response){_this.hideCreateModal(),_this.response=response,_this.getAdmin(_this.currentPage)},function(error){_this.errorCreate=!0,_this.createButton="Save",_this.loadingStatus=!1})},AdminComponent.prototype.showDeleteModal=function(){this.deleteAdminModal.show()},AdminComponent.prototype.hideDeleteModal=function(){this.deleteAdminModal.hide()},AdminComponent.prototype.deleteAdmin=function(){var _this=this;this.loadingStatus=!0,this.deleteButton="Please wait",this.adminService.deleteAdmin(this.deleteId).subscribe(function(response){_this.hideDeleteModal(),_this.response=response,_this.getAdmin(_this.currentPage)},function(error){_this.errorDelete=!0,_this.deleteButton="Delete",_this.loadingStatus=!1})},AdminComponent.prototype.setDeleteId=function(event){var target=event.target||event.srcElement||event.currentTarget;this.deleteId=target.attributes.id.nodeValue,this.showDeleteModal()},__decorate([core_1.ViewChild("createAdminModal"),__metadata("design:type","function"==typeof(_a="undefined"!=typeof modal_component_1.ModalDirective&&modal_component_1.ModalDirective)&&_a||Object)],AdminComponent.prototype,"createAdminModal",void 0),__decorate([core_1.ViewChild("deleteAdminModal"),__metadata("design:type","function"==typeof(_b="undefined"!=typeof modal_component_1.ModalDirective&&modal_component_1.ModalDirective)&&_b||Object)],AdminComponent.prototype,"deleteAdminModal",void 0),__decorate([core_1.ViewChild("updateAdminModal"),__metadata("design:type","function"==typeof(_c="undefined"!=typeof modal_component_1.ModalDirective&&modal_component_1.ModalDirective)&&_c||Object)],AdminComponent.prototype,"updateAdminModal",void 0),AdminComponent=__decorate([core_1.Component({selector:"admin",encapsulation:core_1.ViewEncapsulation.None,styles:[__webpack_require__(1042)],template:__webpack_require__(1047)}),__metadata("design:paramtypes",["function"==typeof(_d="undefined"!=typeof admin_service_1.AdminService&&admin_service_1.AdminService)&&_d||Object,"function"==typeof(_e="undefined"!=typeof error_handler_1.ErrorHandler&&error_handler_1.ErrorHandler)&&_e||Object,"function"==typeof(_f="undefined"!=typeof forms_1.FormBuilder&&forms_1.FormBuilder)&&_f||Object])],AdminComponent);var _a,_b,_c,_d,_e,_f}();exports.AdminComponent=AdminComponent},1027:function(module,exports,__webpack_require__){"use strict";var core_1=__webpack_require__(0),http_1=__webpack_require__(83),default_service_1=__webpack_require__(393),AdminService=function(_super){function AdminService(http){_super.call(this,http),this.http=http}return __extends(AdminService,_super),AdminService.prototype.getAdmin=function(page){return _super.prototype.get.call(this,"rest/admin/"+10*(page-1)+"/10")},AdminService.prototype.getSingleAdmin=function(id){return _super.prototype.get.call(this,"rest/admin/"+id)},AdminService.prototype.getCount=function(){return _super.prototype.get.call(this,"rest/admin/count")},AdminService.prototype.createAdmin=function(c){return _super.prototype.post.call(this,c,"rest/admin")},AdminService.prototype.updateAdmin=function(c){return _super.prototype.put.call(this,c,"rest/admin/"+c.id)},AdminService.prototype.deleteAdmin=function(id){return _super.prototype.delete.call(this,"rest/admin/"+id)},AdminService=__decorate([core_1.Injectable(),__metadata("design:paramtypes",["function"==typeof(_a="undefined"!=typeof http_1.Http&&http_1.Http)&&_a||Object])],AdminService);var _a}(default_service_1.DefaultService);exports.AdminService=AdminService},1036:function(module,exports,__webpack_require__){"use strict";var router_1=__webpack_require__(60),admin_component_1=__webpack_require__(1026),routes=[{path:"",component:admin_component_1.AdminComponent,children:[]}];exports.routing=router_1.RouterModule.forChild(routes)},1037:function(module,exports){"use strict";var Admin=function(){function Admin(id,nama,username,password){this.id=id,this.nama=nama,this.username=username,this.password=password}return Admin}();exports.Admin=Admin},1042:function(module,exports){module.exports="@media screen and (min-width: 1620px) {\n  .row.shift-up > * {\n    margin-top: -573px; } }\n\n@media screen and (max-width: 1620px) {\n  .card.feed-panel.large-card {\n    height: 824px; } }\n"},1047:function(module,exports){module.exports='<div class="row">\n  <div *ngIf="loadingStatus" style="text-align:center;">\n      <br/><h3>Loading</h3><img src="assets/img/loading.svg"/>\n  </div>\n  <div *ngIf="!loadingStatus">\n    <div class="align-right">\n      <button type="button" class="btn btn-primary btn-raised" (click)="showCreateModal()">Tambah</button>\n    </div><br/>\n    <div class="horizontal-scroll">\n      <table class="table table-hover">\n        <thead>\n        <tr class="black-muted-bg">\n          <th>Nama</th>\n          <th>Username</th>\n          <th>Password</th>\n          <th></th>\n        </tr>\n        </thead>\n        <tbody>\n        <tr *ngFor="let c of admin" class="no-top-border">\n          <td>{{c.nama}}</td>\n          <td>{{c.username}}</td>\n          <td>***</td>\n          <td class="align-right">\n            <!--<button type="button" class="btn btn-warning btn-raised" #elem id={{c.id}} (click)="setUpdateId($event)">Ubah</button>-->\n            <button type="button" class="btn btn-danger btn-raised" #elem id={{c.id}} (click)="setDeleteId($event)">Hapus</button>\n          </td>\n        </tr>\n        </tbody>\n      </table>\n    </div>\n    <br/>\n    <div class="col-md-3">\n      <div class="btn-group" role="group">\n        <button *ngFor="let p of pagination.pages" type="button" class="btn btn-primary" (click)="loadPage($event)" [disabled]="currentPage == p">{{p}}</button>\n      </div>\n    </div>\n    <div class="col-md-2">\n      <form [formGroup]="formPage" (ngSubmit)="getAdmin(formPage.controls.page.value)">\n        <div class="input-group">\n          <input type="text" class="form-control" formControlName="page" placeholder="ke halaman...">\n          <span class="input-group-btn">\n              <button class="btn btn-primary" type="submit" [disabled]="!formPage.valid || loadingStatus">Go!</button>\n          </span>\n        </div>\n      </form>\n    </div>\n  </div>\n</div>\n<br/><br/><br/>\n\n<div bsModal #createAdminModal="bs-modal" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true">\n  <div class="modal-dialog modal-md">\n    <div class="modal-content">\n      <div class="modal-header">\n        <button type="button" class="close" (click)="hideCreateModal()" aria-label="Close">\n          <span aria-hidden="true">&times;</span>\n        </button>\n        <h4 class="modal-title">Tambah Data Admin</h4>\n      </div>\n      <form [formGroup]="formCreate" (ngSubmit)="createAdmin(formCreate.value)">\n        <div class="modal-body">\n          <div [hidden]="!errorCreate" class="alert alert-danger">Terjadi kesalahan saat penyimpanan</div>\n          <div class="form-group">\n            <label>Nama</label>\n            <input formControlName="namaC" type="text" class="form-control">\n            <div [hidden]="formCreate.controls.namaC.valid || formCreate.controls.namaC.pristine" class="alert alert-danger">Nama tidak valid</div>\n          </div>\n          <div class="form-group">\n            <label>Username</label>\n            <input formControlName="usernameC" type="text" class="form-control">\n            <div [hidden]="formCreate.controls.usernameC.valid || formCreate.controls.usernameC.pristine" class="alert alert-danger">Username tidak valid</div>\n          </div>\n          <div class="form-group">\n            <label>Password</label>\n            <input formControlName="passwordC" type="password" class="form-control">\n            <div [hidden]="formCreate.controls.passwordC.valid || formCreate.controls.passwordC.pristine" class="alert alert-danger">Password tidak valid</div>\n          </div>\n        </div>\n        <div class="modal-footer">\n          <button type="button" class="btn btn-default btn-raised" (click)="hideCreateModal()" [disabled]="loadingStatus">Cancel</button>\n          <button type="button" class="btn btn-primary btn-raised" type="submit" [disabled]="!formCreate.valid || loadingStatus">{{createButton}}</button>\n        </div>\n      </form>\n    </div>\n  </div>\n</div>\n\n<div bsModal #deleteAdminModal="bs-modal" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true">\n  <div class="modal-dialog modal-md">\n    <div class="modal-content">\n      <div class="modal-header">\n        <button type="button" class="close" (click)="hideDeleteModal()" aria-label="Close">\n          <span aria-hidden="true">&times;</span>\n        </button>\n        <h4 class="modal-title">Hapus Data Admin</h4>\n      </div>\n      <div class="modal-body">\n        <div class="form-group">Yakin akan menghapusnya?</div>\n        <div class="modal-footer">\n          <button type="button" class="btn btn-default btn-raised" (click)="hideDeleteModal()" [disabled]="loadingStatus">Cancel</button>\n          <button type="button" class="btn btn-primary btn-raised" (click)="deleteAdmin()" [disabled]="loadingStatus">{{deleteButton}}</button>\n        </div>\n      </div>\n    </div>\n  </div>\n</div>\n\n<!--<div bsModal #updateAdminModal="bs-modal" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true">\n  <div class="modal-dialog modal-md">\n    <div class="modal-content">\n      <div class="modal-header">\n        <button type="button" class="close" (click)="hideUpdateModal()" aria-label="Close">\n          <span aria-hidden="true">&times;</span>\n        </button>\n        <h4 class="modal-title">Ubah Data Admin</h4>\n      </div>\n      <form [formGroup]="formUpdate" (ngSubmit)="updateAdmin(formUpdate.value)">\n        <div *ngIf="loadingStatusUpdate" style="text-align:center;">\n          <br/><h3>Loading</h3><img src="assets/img/loading.svg"/>\n        </div>\n        <div *ngIf="!loadingStatusUpdate" class="modal-body">\n          <div [hidden]="!errorUpdate" class="alert alert-danger">Terjadi kesalahan saat penyimpanan</div>\n          <div [hidden]="!errorGetUpdate" class="alert alert-danger">Data tidak ditemukan</div>\n          <input formControlName="idU" type="hidden" class="form-control">\n          <div class="form-group">\n            <label>Nama</label>\n            <input formControlName="namaU" type="text" class="form-control">\n            <div [hidden]="formUpdate.controls.namaU.valid || formUpdate.controls.namaU.pristine" class="alert alert-danger">Nama tidak valid</div>\n          </div>\n          <div class="form-group">\n            <label>Username</label>\n            <input formControlName="usernameU" type="text" class="form-control">\n            <div [hidden]="formUpdate.controls.usernameU.valid || formUpdate.controls.usernameU.pristine" class="alert alert-danger">Alamat tidak valid</div>\n          </div>\n          <div class="form-group">\n            <label>Password</label>\n            <input formControlName="passwordU" type="text" class="form-control">\n            <div [hidden]="formUpdate.controls.passwordU.valid || formUpdate.controls.passwordU.pristine" class="alert alert-danger">Nomor Telepon tidak valid</div>\n          </div>\n        </div>\n        <div class="modal-footer">\n          <button type="button" class="btn btn-default btn-raised" (click)="hideUpdateModal()" [disabled]="loadingStatus">Cancel</button>\n          <button type="button" class="btn btn-primary btn-raised" type="submit" [disabled]="!formUpdate.valid || loadingStatus">{{updateButton}}</button>\n        </div>\n      </form>\n    </div>\n  </div>\n</div>-->'}});