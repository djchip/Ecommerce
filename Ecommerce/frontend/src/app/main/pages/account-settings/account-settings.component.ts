import { EventEmitter, Component, Output, OnInit, ViewChild, OnDestroy, ViewEncapsulation } from '@angular/core';

import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { FlatpickrOptions } from 'ng2-flatpickr';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';
import { TokenStorage } from 'app/services/token-storage.service';
import { NavbarNotificationComponent } from 'app/layout/components/navbar/navbar-notification/navbar-notification.component'
import { ToastrService, GlobalConfig } from 'ngx-toastr';
import { AccountSettingsService } from 'app/main/pages/account-settings/account-settings.service';
import { AuthenticationService } from 'app/auth/service';


import { CredentialModel } from 'app/auth/models/credential.model';
import { Router } from '@angular/router';
export enum StorageKey {
  ACCESS_TOKEN = 'accessToken',
  REFRESH_TOKEN = 'refreshToken',
  USER_ROLES = 'userRoles',
  USER_PROFILE = 'userProfile',
  USER_TYPE = 'userType',
  USER_NAME = 'name',
  MENU_LIST = 'menuList',
  PROD_PLAN = 'selectedProductionPlan',
  // DatND
  USER_LOGINID = 'loginId',
  USER_PRIVILEGES = 'privileges',

  LOGIN_TYPE = 'loginType',
}

@Component({
  selector: 'app-account-settings',
  templateUrl: './account-settings.component.html',
  styleUrls: ['./account-settings.component.scss'],
  encapsulation: ViewEncapsulation.None
})

export class AccountSettingsComponent implements OnInit, OnDestroy {
  @ViewChild(NavbarNotificationComponent) private notificationsComponent: NavbarNotificationComponent;
  @Output() afterEditUser = new EventEmitter<string>();
  // public
  public contentHeader: object;
  public data: any;
  public birthDateOptions: FlatpickrOptions = {
    altInput: true
  };
  public passwordTextTypeOld = false;
  public passwordTextTypeNew = false;
  public listRole = [];
  public passwordTextTypeRetype = false;
  public avatarImage: string;
  public addUserForm: FormGroup;
  public passswordForm: FormGroup;
  public resetpassswordForm: FormGroup;
  public listUnit=[];

  public addUserFormSubmitted = false;
  public passwordSubmitted = false;
  public resetpasswordSubmitted = false;
  public userId;
  public username;
  public status;
  public roleLoading = false;

  public currentUserName;
  public mergedPwdShow = false;
  sunmitted = true;
  public currentLang = this._translateService.currentLang

  private _unsubscribeAll: Subject<any>;

  /**
   * Constructor
   *
   * @param {AccountSettingsService} _accountSettingsService
   */
  constructor(     private _router: Router,   private _authenticationService: AuthenticationService,private router: Router, private toastr: ToastrService, private tokenStorage: TokenStorage, public _translateService: TranslateService, private service: AccountSettingsService, private formBuilder: FormBuilder, private _accountSettingsService: AccountSettingsService) {
    this._unsubscribeAll = new Subject();
  }


  togglePasswordTextTypeOld() {
    this.passwordTextTypeOld = !this.passwordTextTypeOld;
  }


  togglePasswordTextTypeNew() {
    this.passwordTextTypeNew = !this.passwordTextTypeNew;
  }

  togglePasswordTextTypeRetype() {
    this.passwordTextTypeRetype = !this.passwordTextTypeRetype;
  }


  ngOnInit() {
    this._accountSettingsService.onSettingsChanged.pipe(takeUntil(this._unsubscribeAll)).subscribe(response => {
      this.data = response;
      this.avatarImage = this.data.accountSetting.general.avatar;
      this.initForm();
      this.initPasswordForm();

      this.username = window.localStorage.getItem("username");
      console.log(this.username);

      this.currentUserName = this.tokenStorage.getUsername();
      
      this.getListRole();
      this.getListUnit();
      this.getUserDetail();
      console.log(this.currentUserName);
      console.log(this.listUnit+"oke");
      



    });

    

    // content header
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.ACCOUNT_SETTING',
      actionButton: true,
      breadcrumb: {
        type: '',
        links: [
          {
            name: 'CONTENT_HEADER.MAIN_PAGE',
            isLink: true,
            link: '/dashboard'
          },
          {
            name: 'CONTENT_HEADER.ACCOUNT_SETTING',
            isLink: false
          }
        ]
      }
    };
  }

  /**
   * On destroy
   */
  ngOnDestroy(): void {
    // Unsubscribe from all subscriptions
    this._unsubscribeAll.next();
    this._unsubscribeAll.complete();
  }

  initForm() {
    this.addUserForm = this.formBuilder.group(
      {
        id: [""],
        username: [""],
        password: ['', [Validators.pattern("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")]],
        fullname: ["", Validators.required],
        phoneNumber: ['', [Validators.pattern('^(0?)([1-9][2-9]|5[6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])[0-9]{7}$')]],
        email: ["", [Validators.required, Validators.pattern(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/)]],
        // roleName: [ "", Validators.required],
        role: ["", Validators.required],
        unit:[null, Validators.required],

        //change_password
        // oldPassword :["", Validators.required ],
        // newPassword:[ "", [Validators.pattern("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")]],
        // confirmPassword: [ "", [Validators.pattern("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")]],

      },
    );

  }
  initPasswordForm() {
    this.passswordForm = this.formBuilder.group(
      {
        //change_password
        oldPassword: ["", Validators.required],
        newPassword: ["", [Validators.pattern("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")]],
        confirmPassword: ["", [Validators.pattern("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")]],
      },
    );

  }



  fillForm() {
    this.addUserForm.patchValue(
      {
        id: this.data.id,
        username: this.data.username,
        fullname: this.data.fullname,
        phoneNumber: this.data.phoneNumber,
        email: this.data.email,
        roleName: this.data.roleName,
        role: this.data.role,
        unit: this.data.unit,

      },
    );
    console.log(this.addUserForm.value.unit.id+"unit.id")
  }

  fillPasswordForm() {
    this.passswordForm.patchValue(
      {

        //change_password
        oldPassword: this.data.oldPassword,
        newPassword: this.data.newPassword,
        confirmPassword: this.data.confirmPassword,
      },
    );
    console.log(this.passswordForm.value)
  }

  fillResetPasswordForm() {
    this.resetpassswordForm.patchValue(
      {

        //change_password
        newPassword: this.data.newPassword,
        confirmPassword: this.data.confirmPassword,
      },
    );
    console.log(this.resetpassswordForm.value)
  }

  get AddUserForm() {
    return this.addUserForm.controls;
  }
  get changePassForm() {
    return this.passswordForm.controls;
  }

  get resetchangePassForm() {
    return this.resetpassswordForm.controls;
  }

  async getUserDetail() {
    if (this.username !== '') {
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      await this.service
        .findbyName(params, this.currentUserName)
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



  editPassword() {
    this.passwordSubmitted = true;

    if (this.passswordForm.value.password !== '') {
      this.passswordForm.patchValue({
        password: this.addUserForm.value.password.trim()
      })
    }

    if (this.passswordForm.invalid) {
      return;
    }

    let content = this.passswordForm.value;
    // content.roleId = this.addUserForm.value.role.form_id;
    // this.roleId=content.roleId;

    let params = {
      method: "POST",
      content: content
    };
    console.log('params', params)
    Swal.showLoading();
    this.service
      .changepassword(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.UPDATE_PAS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),

          }).then((result) => {
            // this.afterEditUser.emit('completed');
            this._authenticationService.logout();
          this._router.navigate(['/pages/authentication/login-v2']);
          });

        } else if (response.code === 5) {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.INFORMATION_NULL'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
          });
        }
        else if (response.code === 10) {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.EROR_PASS1'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
          });
        }
        else if (response.code === 11) {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.EROR_PASS2'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
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

  editUser() {
    this.addUserFormSubmitted = true;

    if (this.addUserForm.value.fullname !== '') {
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
    console.log('params', params)
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

          });
          // this.router.navigate(["/admin/user/list-user"]);
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
          // console.log(this.listRole);

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

  resetForm() {
    this.ngOnInit();
    this.addUserForm.reset();
    this.sunmitted = false;

  }
  resetFormtPassword() {
    this.ngOnInit();
    this.passswordForm.reset();
    this.sunmitted = false;

  }
  resetFormResetPassword() {
    this.ngOnInit();
    this.resetpassswordForm.reset();
    this.sunmitted = false;

  }

  doThis(notifyTopic) {
    let notifyObject = JSON.parse(notifyTopic.body);
    if (this.currentUserName == notifyObject.username) {
      this.notificationsComponent.getListNotifications();
      this.toastrMessage(notifyObject.message);
    }

  }
  toastrMessage(message: string) {
    this.toastr.info(message, 'Thông báo!', {
      toastClass: 'toast ngx-toastr',
      closeButton: true
    });
  }

  logout() {
    this._authenticationService.logout();
    this._router.navigate(['/pages/authentication/login-v2']);
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
}
