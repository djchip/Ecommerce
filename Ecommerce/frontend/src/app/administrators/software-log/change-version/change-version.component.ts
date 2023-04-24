import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { SoftwareLogService } from '../software-log.service';
import { TranslateService } from '@ngx-translate/core';
import { CoreTranslationService } from '@core/services/translation.service';
@Component({
  selector: 'app-change-version',
  templateUrl: './change-version.component.html',
  styleUrls: ['./change-version.component.scss']
})
export class ChangeVersionComponent implements OnInit {

  @Output() afterCreateVersion = new EventEmitter<string>();

  public contentHeader: object;
  public addVersionForm: FormGroup;
  public addVersionFormSubmitted = false;
  public mergedPwdShow = false;

  constructor(private formBuilder: FormBuilder, private service: SoftwareLogService, 
    private _coreTranslationService: CoreTranslationService,public _translateService: TranslateService) { }

  ngOnInit(): void {
  this.initForm();
  }
initForm() {
  this.addVersionForm = this.formBuilder.group(
    {
      version: ['',[Validators.required]],
      changeLogs: ['',[Validators.required]],
      // lastestVersion: ['',[Validators.required]],
    },
  );
}

get AddVersionForm(){
  return this.addVersionForm.controls;
}
addVersion() {
  this.addVersionFormSubmitted = true;
  if(this.addVersionForm.value.version !== ''){
    this.addVersionForm.patchValue({
      version: this.addVersionForm.value.version.trim()
    })
  }
  if(this.addVersionForm.value.changeLogs !== ''){
    this.addVersionForm.patchValue({
      changeLogs: this.addVersionForm.value.changeLogs.trim()
    })
  }
  // if(this.addVersionForm.value.lastestVersion !== ''){
  //   this.addVersionForm.patchValue({
  //     lastestVersion: this.addVersionForm.value.lastestVersion.trim()
  //   })
  // }
  if (this.addVersionForm.invalid) {
    return;
  }

  let content = this.addVersionForm.value;

  let params = {
    method: "POST",
    content: content,
  };
  Swal.showLoading();
  this.service
    .addVersion(params)
    .then((data) => {
      Swal.close();
      let response = data;
      if (response.code === 0) {
        Swal.fire({
          icon: "success",
          title:  this._translateService.instant('CHANGE_GROUP.ADD_SUCCESS'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        }).then((result) => {
          // this.initForm();
          this.afterCreateVersion.emit('completed');
        });
      } else if(response.code ===3){
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('CHANGE_GROUP.CHANGE_GROUP_EXISTED'),
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
