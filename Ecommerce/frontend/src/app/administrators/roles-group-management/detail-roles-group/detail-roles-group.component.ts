import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { RolesGroupManagementService } from '../roles-group-management.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-detail-roles-group',
  templateUrl: './detail-roles-group.component.html',
  styleUrls: ['./detail-roles-group.component.scss']
})
export class DetailRolesGroupComponent implements OnInit {

  public contentHeader: object;
  // public roleId;
  public data: any;
  public currentLang = this._translateService.currentLang

  @Input() id;
  @Input() obj;
  @Input() objRevert;
public dataRevert = {
    id:null,
    roleCode: null,
    roleName: null,
    roleNameEn: null,

    createdBy: null,
    updatedBy: null,
    createdDate: null,
    updatedDate: null,
    status:null,
  
 };

  constructor(public _translateService: TranslateService,private router: Router, private service: RolesGroupManagementService) { }

  ngOnInit(): void {
    this.contentHeader = {
      headerTitle: 'Thông tin người dùng',
      actionButton: true,
      breadcrumb: {
        type: '',
        links: [
          {
            name: 'Trang chủ',
            isLink: true,
            link: '/'
          },
          {
            name: 'Quản trị hệ thống',
            isLink: true,
            link: '/'
          },
          {
            name: 'Quản lý nhóm quyền',
            isLink: false
          }
        ]
      }
    };
    if( this.id != null){
      this.getRoleDetail();

    } else{
      this.data=this.obj;
      if(this.objRevert !== null){
        this.compareObj();
      }
    }
  }
  getRoleDetail(){
    if(this.id !== ''){
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      this.service
        .detailRoles(params, this.id)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
          } else {
            Swal.fire({
              icon: "error",
              title: response.errorMessages,
            });
          }
        })
        .catch((error) => {
          Swal.close();
          Swal.fire({
            icon: "error",
            title: "Can not reach Gateway.",
            confirmButtonText: "OK",
          });
        });
    }
  }

  // editUser(){
  //   this.router.navigate(["/admin/user/edit-user"]);
  // }

  // deleteUser(){
  //   Swal.fire({
  //     title: 'Are you sure?',
  //     text: "You won't be able to revert this!",
  //     icon: 'warning',
  //     showCancelButton: true,
  //     confirmButtonText: 'Yes, delete it!',
  //     customClass: {
  //       confirmButton: 'btn btn-primary',
  //       cancelButton: 'btn btn-danger ml-1'
  //     }
  //   }).then((result) => {
  //     if (result.value) {
  //       Swal.fire({
  //         title: 'Deleted!',
  //         text: 'Your file has been deleted.',
  //         icon: 'success',
  //         customClass: {
  //           confirmButton: 'btn btn-success'
  //         }
  //       }).then((result) => {
  //         this.router.navigate(["/admin/user/list-user"]);    
  //       });
  //     }
  //   });
  // }

  compareObj() {
    this.obj.id === this.objRevert.id ? this.dataRevert.id = null : this.dataRevert.id = this.objRevert.id;
    this.obj.roleCode === this.objRevert.roleCode ? this.dataRevert.roleCode = null : this.dataRevert.roleCode = this.objRevert.roleCode;
    this.obj.roleName === this.objRevert.roleName ? this.dataRevert.roleName = null : this.dataRevert.roleName = this.objRevert.roleName;
    this.obj.roleNameEn === this.objRevert.roleNameEn ? this.dataRevert.roleNameEn = null : this.dataRevert.roleNameEn = this.objRevert.roleNameEn;
    this.obj.createdBy === this.objRevert.createdBy ? this.dataRevert.createdBy = null : this.dataRevert.createdBy = this.objRevert.createdBy;
    this.obj.updatedBy === this.objRevert.updatedBy ? this.dataRevert.updatedBy = null : this.dataRevert.updatedBy = this.objRevert.updatedBy;
    this.obj.createdDate === this.objRevert.createdDate ? this.dataRevert.createdDate = null : this.dataRevert.createdDate = this.objRevert.createdDate;
    this.obj.updatedDate === this.objRevert.updatedDate ? this.dataRevert.updatedDate = null : this.dataRevert.updatedDate = this.objRevert.updatedDate;
    this.obj.status === this.objRevert.status ? this.dataRevert.status = null : this.dataRevert.status = this.objRevert.status;

  }

}
