import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { RolesGroupManagementService } from '../roles-group-management.service';
import { TranslateService } from '@ngx-translate/core';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { CoreTranslationService } from '@core/services/translation.service';


@Component({
  selector: 'app-add-roles-group',
  templateUrl: './add-roles-group.component.html',
  styleUrls: ['./add-roles-group.component.scss']
})
export class AddRolesGroupComponent implements OnInit {

  @Output() afterCreateRoles = new EventEmitter<string>();

  public contentHeader: object;
  public listRole = [];
  public roleLoading = false;
  public addRolesForm: FormGroup;
  public addRolesFormSubmitted = false;
  public mergedPwdShow = false;
  
  constructor(private formBuilder: FormBuilder, private service: RolesGroupManagementService, 
    private _coreTranslationService: CoreTranslationService,public _translateService: TranslateService) { }

  ngOnInit(): void {
    this.contentHeader = {
      headerTitle: 'Thêm mới người dùng',
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
    this._coreTranslationService.translate(eng, vie);
    this.initForm();
    
    // this.getListRole();
  }
  initForm(){
    this.addRolesForm = this.formBuilder.group(
      {
        roleCode: ['',[Validators.required]],
        roleName: ['',[Validators.required]],
        roleNameEn: [''],
      },
    );
  }
  removeSpaces(control: AbstractControl) {
    if (control && control.value && !control.value.replace(/\s/g, '').length) {
      control.setValue('');
    }
    return null;
  }

  get AddRolesForm(){
    return this.addRolesForm.controls;
  }

  addRoles(){
    this.addRolesFormSubmitted = true;
    if(this.addRolesForm.value.roleCode !== ''){
      this.addRolesForm.patchValue({
        roleCode: this.addRolesForm.value.roleCode.trim()
      })
    }
    if(this.addRolesForm.value.roleName !== ''){
      this.addRolesForm.patchValue({
        roleName: this.addRolesForm.value.roleName.trim()
      })
    }
    // if(this.addRolesForm.value.roleNameEn !== ''){
    //   this.addRolesForm.patchValue({
    //     roleNameEn: this.addRolesForm.value.roleNameEn.trim()
    //   })
    // }
    if (this.addRolesForm.invalid) {
      return;
    }

    let content= this.addRolesForm.value;
    // content.roleId = this.addRolesForm.value.role.id;

    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
      .addRoles(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title:  this._translateService.instant('MESSAGE.ROLES_GROUP.ADD_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            // this.initForm();
            this.afterCreateRoles.emit('completed');
          });
        } else if(response.code ===3){
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.ROLES_GROUP.ROLE_GROUP_EXISTED'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
          });
        }
        else {
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
