import { ChangeLanguageService } from './../../../services/change-language.service';
import { TranslateService } from '@ngx-translate/core';
import { FormService } from './../form.service';
import { Component, OnInit, Output, EventEmitter, Input, ViewEncapsulation } from '@angular/core';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-copy-multi-form',
  templateUrl: './copy-multi-form.component.html',
  styleUrls: ['./copy-multi-form.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class CopyMultiFormComponent implements OnInit {
  @Output() afterCopyForm = new EventEmitter<string>();
  @Input() rows;
  backup;
  public currentLang = this._translateService.currentLang;
  constructor(private service: FormService, public _translateService: TranslateService, private _changeLanguageService: ChangeLanguageService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    });
  }

  ngOnInit(): void {
    this.backup = this.rows;
  }

  resetForm() {
    this.rows = this.backup;
  }

  copyMulti() {
    let forms = this.rows;
    for(let i = 0; i < forms.length; i++){
      if(forms[i].yearOfApplication == ""){
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('MESSAGE.FORM.YEAR'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        }).then((result) => {
        });
        return;
      }
    }
    let params = {
      method: "POST",
      content: forms,
    };

    for (let i = 0; i < forms.length; i++) {
      
      let inputDate = new Date(forms[i].uploadTime);
      let date = new Date();
      date.setHours(0);
      date.setMinutes(0);
      date.setSeconds(0);
      date.setMilliseconds(0);
      if (inputDate.getTime() < date.getTime()) {
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('MESSAGE.FORM.INVALID_YEAR'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        }).then((result) => {
        });
        return;
      }
    }
    Swal.showLoading();
    this.service
      .copyForms(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant("MESSAGE.FORM.COPY_SUCCESS"),
            confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
          }).then((result) => {
            this.afterCopyForm.emit("completed");
          });
        }
      })
      .catch((error) => {
        Swal.fire({
          icon: "error",
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      });
  }
}
