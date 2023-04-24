import { Component, OnInit } from '@angular/core';
import { CoreConfigService } from '@core/services/config.service';
import { Router } from "@angular/router";
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { ForgotPasswordConfirmService } from './forgot-password-confirm.service'

@Component({
  selector: 'app-forgot-password-confirm',
  templateUrl: './forgot-password-confirm.html',
  styleUrls: ['./forgot-password-confirm.scss']
})
export class ForgotPasswordConfirmComponent implements OnInit {
  public coreConfig: any;

  // Private
  private _unsubscribeAll: Subject<any>;

  /**
   * Constructor
   *
   * @param {CoreConfigService} _coreConfigService
   */
  constructor(private _coreConfigService: CoreConfigService, private router:Router, private service:ForgotPasswordConfirmService) {
    this._unsubscribeAll = new Subject();

    // Configure the layout
    this._coreConfigService.config = {
      layout: {
        navbar: {
          hidden: true
        },
        footer: {
          hidden: true
        },
        menu: {
          hidden: true
        },
        customizer: false,
        enableLocalStorage: false
      }
    };
  }

  // Lifecycle Hooks
  // -----------------------------------------------------------------------------------------------------

  /**
   * On init
   */
  ngOnInit(): void {
    // Subscribe to config changes
    // debugger;
    this._coreConfigService.config.pipe(takeUntil(this._unsubscribeAll)).subscribe(config => {
      this.coreConfig = config;
    });
    let url = this.router.url;
    let token = url.replace("/pages/miscellaneous/forgot-password-confirm?token=", "");
    let params = {method: "POST", content: {token:token}}
    this.service.forgotpasswordConfirm(params);
  }

  /**
   * On destroy
   */
  ngOnDestroy(): void {
    // Unsubscribe from all subscriptions
    this._unsubscribeAll.next();
    this._unsubscribeAll.complete();
  }

  gotoLoginPage(){
    this.router.navigate(["/pages/authentication/login-v2"]);
  }
}
