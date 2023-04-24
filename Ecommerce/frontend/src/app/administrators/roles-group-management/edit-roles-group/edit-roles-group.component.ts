import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { RolesGroupManagementService } from '../roles-group-management.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-edit-roles-group',
  templateUrl: './edit-roles-group.component.html',
  styleUrls: ['./edit-roles-group.component.scss']
})
export class EditRolesGroupComponent implements OnInit {

  @Output() afterEditRoles = new EventEmitter<string>();

  public contentHeader: object;
  public listStatus = [{id:"0", name: "Deleted"}, {id:"1", name: "Active"}, {id:"2", name: "Locked"}];
  public roleLoading = false;
  public addRolesForm: FormGroup;
  public addRolesFormSubmitted = false;
  public data;
  public roleId;
  public roleName;
  public status;
  public mergedPwdShow = false;
  constructor(private formBuilder: FormBuilder, private service: RolesGroupManagementService, public _translateService: TranslateService) { }

  ngOnInit(): void {
    this.contentHeader = {
      headerTitle: 'Cập nhật người dùng',
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
    // this.getListRole();
    this.initForm()
    this.roleId = window.localStorage.getItem("roleId");
    this.getRolesDetail();
  }

  initForm(){
    this.addRolesForm = this.formBuilder.group(
      {
        id: ['',[Validators.required]],
        roleCode: ['',[Validators.required]],
        roleName: ['',[Validators.required]],
        roleNameEn: [''],
      },
    );
  }
  fillForm(){
    this.addRolesForm.patchValue(
      {
        id: this.data.id,
        roleCode: this.data.roleCode,
        roleName: this.data.roleName,
        roleNameEn: this.data.roleNameEn,
        status: this.status
      },
    );
  }

  get AddRolesForm(){
    return this.addRolesForm.controls;
  }

  async getRolesDetail(){
    if(this.roleId !== ''){
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      await this.service
        .detailRoles(params, this.roleId)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
            this.listStatus.forEach(element =>{
              if(element.id == this.data.status + ""){
                this.status = element;
              }
            })
            this.fillForm();
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
            title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          });
        });
    }
  }

  editRoles(){
    this.addRolesFormSubmitted = true;

    if(this.addRolesForm.value.roleName !== ''){
      this.addRolesForm.patchValue({
        roleName: this.addRolesForm.value.roleName.trim()
      })
    }

    console.log(JSON.stringify(this.addRolesForm.value))

    if (this.addRolesForm.invalid) {
      return;
    }

    let content = this.addRolesForm.value;
    // content.roleId = this.addRolesForm.value.role.id;
    content.status = this.addRolesForm.value.status;
    // content.roleName = this.addRolesForm.value.roleName;

    let params = {
      method: "PUT",
      content: content
    };
    Swal.showLoading();
    this.service
      .editRoles(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.ROLES_GROUP.UPDATE_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.afterEditRoles.emit('completed');
          });
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
          title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        });
      });

  }

  resetForm(){
    this.ngOnInit();
  }


}
