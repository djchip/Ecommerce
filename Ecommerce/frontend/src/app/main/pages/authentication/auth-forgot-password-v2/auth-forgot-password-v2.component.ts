import { EventEmitter,Output,Component, OnInit, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from "@angular/router";
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import Swal from "sweetalert2";
import { CoreConfigService } from '@core/services/config.service';
import { ForgotPasswordService } from 'app/main/pages/authentication/auth-forgot-password-v2/forgot-password.service';

import { TranslateService } from '@ngx-translate/core';
@Component({
  selector: 'app-auth-forgot-password-v2',
  templateUrl: './auth-forgot-password-v2.component.html',
  styleUrls: ['./auth-forgot-password-v2.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class AuthForgotPasswordV2Component implements OnInit {
  @Output() afterEditUser = new EventEmitter<string>();
  // Public
  public emailVar;
  public coreConfig: any;
  public forgotPasswordForm: FormGroup;
  public forgotpasswordSubmitted = false;
  public submitted = false;
  public data: any;
  
  // Private
  private _unsubscribeAll: Subject<any>;

  /**
   * Constructor
   *
  //  * 
   * @param {FormBuilder} _formBuilder
   *@param {ForgotPasswordService} service
   */
  
  constructor(  
    private route : Router,
    public _translateService: TranslateService,
    private service: ForgotPasswordService,
    private formBuilder: FormBuilder,
    private _coreConfigService: CoreConfigService, 
    private _formBuilder: FormBuilder) 
    {
    this._unsubscribeAll = new Subject();

    // Configure the layout
    this._coreConfigService.config = {
      layout: {
        navbar: {
          hidden: true
        },
        menu: {
          hidden: true
        },
        footer: {
          hidden: true
        },
        customizer: false,
        enableLocalStorage: false
      }
    };
  }

  // convenience getter for easy access to form fields
  get ForgotPassword() {
    return this.forgotPasswordForm.controls;
  }

  /**
   * On Submit
   */
  onSubmit() {
    this.submitted = true;

    // stop here if form is invalid
    if (this.forgotPasswordForm.invalid) {
      return;
    }
  }

  // Lifecycle Hooks
  // -----------------------------------------------------------------------------------------------------

  /**
   * On init
   */
  ngOnInit(): void {
    // this.forgotPasswordForm = this._formBuilder.group({
    //   email: ['', [Validators.required, Validators.email]]
    // });

    // Subscribe to config changes
    this._coreConfigService.config.pipe(takeUntil(this._unsubscribeAll)).subscribe(config => {
      this.coreConfig = config;
    });

      
      this.initForm();




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
    this.forgotPasswordForm = this.formBuilder.group(
      {
         email: ["", [Validators.required, Validators.pattern(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/)]],
  

      },
    );

  }

  fillForm() {
    this.forgotPasswordForm.patchValue(
      {

        email: this.data.email,
      },
    );
    console.log(this.forgotPasswordForm.value)
  }

  resetPassword() {
    this.forgotpasswordSubmitted = true;

    if (this.forgotPasswordForm.value.email !== '') {
      this.forgotPasswordForm.patchValue({
        email: this.forgotPasswordForm.value.email.trim()
      })
    }

    if (this.forgotPasswordForm.invalid) {
      return;
    }

    let content = this.forgotPasswordForm.value;


    let params = {
      method: "POST",
      content: content
    };
    console.log('params', params)
    Swal.showLoading();
    this.service
      .forgotpassword(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.route.navigate(["/pages/miscellaneous/forgot-password-sent"]);
        } 
        else if (response.code === 5) {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('No email exists'),
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
}
