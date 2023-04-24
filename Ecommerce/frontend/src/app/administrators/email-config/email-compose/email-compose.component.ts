import { Component, HostListener, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';

import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { EmailService } from 'app/main/apps/email/email.service';
import Swal from "sweetalert2";
import {EmailConfigService} from "../email-config.service";
import {CoreTranslationService} from "../../../../@core/services/translation.service";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-email-compose',
  templateUrl: './email-compose.component.html',
  // styleUrls: ['./email-compose.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class EmailComposeComponent implements OnInit {

  // Decorator
  @HostListener('keydown.escape') fn() {
    this.closeCompose();
  }
  @ViewChild('selectRef') private _selectRef: any;

  // Public
  public emailToSelect = [
    'tungnm2@neo.vn',
    'haild@neo.vn',
    'wtungnm9899@gmail.com',
  ];

  public emailCCSelect = [
    'tiennv@neo.vn',
    'haild@neo.vn',
    'wtungnm9899@gmail.com',
  ];

  public emailBCCSelect = [
    { name: 'Jane Foster', avatar: 'assets/images/portrait/small/avatar-s-3.jpg' },
    { name: 'Donna Frank', avatar: 'assets/images/portrait/small/avatar-s-1.jpg' },
    { name: 'Gabrielle Robertson', avatar: 'assets/images/portrait/small/avatar-s-4.jpg' },
    { name: 'Lori Spears', avatar: 'assets/images/portrait/small/avatar-s-6.jpg' }
  ];

  public emailCCSelected = [];
  public emailBCCSelected = [];

  public isOpenCC = false;
  public isOpenBCC = false;

  public isComposeOpen: boolean = false;
  public fileUpload: File;
  public quillEditorContent = '';
  public emailSubject = '';
  public emailToSelected = [];
  public ccSelected = [];

  // Private
  private _unsubscribeAll: Subject<any>;

  /**
   *
   * @param {EmailService} _emailService
   */
  constructor(private _emailService: EmailService,
              private service: EmailConfigService,
              private _coreTranslationService: CoreTranslationService,
              public _translateService: TranslateService) {
    this._unsubscribeAll = new Subject();
  }

  // Public Methods
  // -----------------------------------------------------------------------------------------------------

  /**
   * Toggle CC & BCC
   *
   * @param toggleRef
   */
  togglCcBcc(toggleRef) {
    if (toggleRef == 'cc') {
      this.isOpenCC = !this.isOpenCC;
    } else {
      this.isOpenBCC = !this.isOpenBCC;
    }
  }

  /**
   * Close Compose
   */
  closeCompose() {
    this.isComposeOpen = false;
    this._emailService.composeEmail(this.isComposeOpen);
  }

  send() {
    // console.log("quillEditorContent",this.quillEditorContent);
    // console.log("emailSubject",this.emailSubject);
    // console.log("emailToSelected",this.emailToSelected);
    // console.log("ccSelected",this.ccSelected);
    // console.log(Array.from(this.emailToSelected));
    this.sendEmail();
    this.isComposeOpen = false;
    this._emailService.composeEmail(this.isComposeOpen);
  }

  sendEmail() {
    let params = {
      method: "GET",
      cc: this.ccSelected,
      subject: this.emailSubject,
      textContent: this.quillEditorContent,
      to: this.emailToSelected
    };
    console.log("params",params);
    // return;
    Swal.showLoading();
    this.service
        .sendEmail(params, this.fileUpload)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            console.log("==========================DATA", data);
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

  ngOnInit(): void {
    // Subscribe to Compose Model Changes
    this._emailService.composeEmailChanged.pipe(takeUntil(this._unsubscribeAll)).subscribe(response => {
      this.isComposeOpen = response;
      if (this.isComposeOpen) {
        setTimeout(() => {
          this._selectRef.searchInput.nativeElement.focus();
        }, 0);
      }
    });
  }

  /**
   * On destroy
   */

  ngOnDestroy(): void {
    // Unsubscribe from all subscriptions
    this._unsubscribeAll.next();
    this._unsubscribeAll.complete();
  }

}
