import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { UserManagementService } from '../user-management.service';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';
import { TokenStorage } from 'app/services/token-storage.service';

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.scss']
})
export class EditUserComponent implements OnInit {

  @Output() afterEditUser = new EventEmitter<string>();

  public contentHeader: object;
  public listRole = [];
  public listUnit = [];
  public listStatus = [{id:"0", name: "Deleted"}, {id:"1", name: "Active"}, {id:"2", name: "Locked"}];
  public roleLoading = false;
  public addUserForm: FormGroup;
  public addUserFormSubmitted = false;
  public data;
  public userId;
  public status;
  public isAdmin = false;
  public listUserId = [];
  public currentUserName;
  public mergedPwdShow = false;
  public currentLang = this._translateService.currentLang
  constructor(private tokenStorage: TokenStorage,private formBuilder: FormBuilder, private service: UserManagementService,  public _translateService: TranslateService) { }

  ngOnInit(): void {
    this.currentUserName = this.tokenStorage.getUsername();
    this.getUserIdByRolesid();
    this.getListRole();
    this.getListUnit();
    this.initForm();
    this.userId = window.localStorage.getItem("userId");
    this.getUserDetail();
  }

  initForm(){
    this.addUserForm = this.formBuilder.group(
      {
        id: ["", Validators.required],
        username: ["", Validators.required],
        // password: ['', [Validators.pattern("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")]],
        fullname: ["", Validators.required ],
        phoneNumber: ['',[Validators.pattern('^(0?)([1-9][2-9]|5[6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])[0-9]{7}$')]],
        email: ["", [Validators.required,  Validators.pattern(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/)]],
        // roleName: [ "", Validators.required],
        role: [null, Validators.required],
        unit: [null, Validators.required]
      },
    );
  }

  getListRole(){
    let params = {
      method: "GET"
    };
    this.service
      .getListRole(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listRole = response.content;

          
          this.listRole.forEach((item, index) => {
     
            if (this.isAdmin) {
              if (item.roleCode === "Super Admin") {
                // console.log(item.roleCode+"= roleCode 2");
                // console.log(index); 
                this.listRole.splice(index ,1);

              }
            
          }
        })

        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
          if(response.code === 2){
            this.listRole = [];
          }
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

  fillForm(){
    this.addUserForm.patchValue(
      {
        id: this.data.id,
        username: this.data.username,
        fullname: this.data.fullname,
        phoneNumber: this.data.phoneNumber,
        email: this.data.email,
        roleName: this.data.roleName,
        status: this.status,
        role: this.data.role,
        unit: this.data.unit
      },
    );
  }

  get AddUserForm(){
    return this.addUserForm.controls;
  }



  async getUserDetail(){
    if(this.userId !== ''){
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      await this.service
        .detailUser(params, this.userId)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
            // this.listRole.forEach(element =>{
            //   if(element.id == this.data.roleId + ""){
            //     this.roleName = element;
            //   }
            // }
            // )

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

  editUser(){
    this.addUserFormSubmitted = true;

    if(this.addUserForm.value.fullname !== ''){
      this.addUserForm.patchValue({
        fullname: this.addUserForm.value.fullname.trim()
      })
    }

    if (this.addUserForm.invalid) {
      return;
    }

    let content = this.addUserForm.value;
    // content.roleId = this.addUserForm.value.role.form_id;
    // this.roleId=content.roleId;

    let params = {
      method: "PUT",
      content: content
    };
    Swal.showLoading();
    this.service
      .editUser(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.UPDATE_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.afterEditUser.emit('completed');
          });
        }else if (response.code === 6) {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.EMAIL_EXISTED'),
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
    this.fillForm()
  }

  getListUnit(){
    let params = {
      method: "GET"
    };
    this.service
      .getListUnit(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listUnit = response.content;
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
          if(response.code === 2){
            this.listUnit = [];
          }
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
  getUserIdByRolesid() {
    let params = { method: "GET" }
    this.service
      .getUserIdByRolesid(params, this.currentUserName)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listUserId = response.content;
          if (this.listUserId.length > 0) {
            this.listUserId.forEach((item) => {
              if (item == 1) {
                this.isAdmin = true;
              }

            })
          }
        } else {
          this.listUserId = [];
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      })
  }
}