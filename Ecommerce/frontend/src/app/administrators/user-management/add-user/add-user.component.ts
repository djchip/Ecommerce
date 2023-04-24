import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { UserManagementService } from '../user-management.service';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';
import { TokenStorage } from 'app/services/token-storage.service';

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.scss']
})
export class AddUserComponent implements OnInit {

  @Output() afterCreateUser = new EventEmitter<string>();

  public contentHeader: object;
  public listRole = [];
  public listUnit = [];
  public roleLoading = false;
  public addUserForm: FormGroup;
  public addUserFormSubmitted = false;
  public mergedPwdShow = false;
  public listUserId = [];
  public ArrayRole = [];
  public isAdmin = false;
  public currentUserName;
  public currentLang = this._translateService.currentLang
  constructor(private tokenStorage: TokenStorage, private formBuilder: FormBuilder, private service: UserManagementService, public _translateService: TranslateService) {
  }

  ngOnInit(): void {
    this.currentUserName = this.tokenStorage.getUsername();
    this.getUserIdByRolesid();

    this.initForm();
    this.getListRole();
    this.getListUnit();
  }

  initForm() {
    this.addUserForm = this.formBuilder.group(
      {
        username: ['', [Validators.required]],
        password: ['', [Validators.required, Validators.pattern("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")]],
        fullname: ['', [Validators.required]],
        phoneNumber: ['', [Validators.pattern('^(0?)([1-9][2-9]|5[6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])[0-9]{7}$')]],
        email: ['', [Validators.required, Validators.pattern(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/)]],
        role: [null, Validators.required],
        unit: [null, Validators.required],
      },
    );
  }
  removeSpaces(control: AbstractControl) {
    if (control && control.value && !control.value.replace(/\s/g, '').length) {
      control.setValue('');
    }
    return null;
  }

  get AddUserForm() {
    return this.addUserForm.controls;
  }

  getListRole() {
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
          if (response.code === 2) {
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

  getListUnit() {
    let params = {
      method: "GET"
    };
    this.service
      .getListUnit(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listUnit = response.content;
          console.log(this.listRole);

        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
          if (response.code === 2) {
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

  addUser() {
    this.addUserFormSubmitted = true;
    if (this.addUserForm.value.username !== '') {
      this.addUserForm.patchValue({
        username: this.addUserForm.value.username.trim()
      })
    }
    if (this.addUserForm.value.fullname !== '') {
      this.addUserForm.patchValue({
        fullname: this.addUserForm.value.fullname.trim()
      })
    }
    if (this.addUserForm.invalid) {
      return;
    }

    let content = this.addUserForm.value;
    content.unitId = this.addUserForm.value.unit.id;
    console.log('type' + typeof (content.role));

    let params = {
      method: "POST",
      content: content,
    };

    Swal.showLoading();
    this.service
      .addUser(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.ADD_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            // this.initForm();
            this.afterCreateUser.emit('completed');
          });
        } else if (response.code === 3) {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.USERNAME_EXISTED'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
          });
        } else if (response.code === 6) {
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

  getUserIdByRolesid() {
    let params = { method: "GET" }
    this.service
      .getUserIdByRolesid(params, this.currentUserName)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listUserId = response.content;
          console.log(this.listUserId+ "= list user");
          
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
