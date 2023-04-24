import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { SoftwareLogService } from '../software-log.service';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';
import { ChangeLanguageService } from 'app/services/change-language.service';

@Component({
  selector: 'app-add-software-log',
  templateUrl: './add-software-log.component.html',
  styleUrls: ['./add-software-log.component.scss']
})
export class AddSoftwareLogComponent implements OnInit {
  @Output() afterCreateSoftwareLog = new EventEmitter<string>();

  public addErrorLogForm: FormGroup;
  public addErrorLogSubmitted = false;
  public currentLang = this._translateService.currentLang;
  public listStatus = [{id:"0", nameEn: "Bug", name: "Lỗi"}, 
                      {id:"1", nameEn: "Fixed", name: "Đã sửa"}, 
                      {id:"2", nameEn: "Closed", name: "Đã đóng"},
                      {id:"3", nameEn: "Cancel", name: "Làm lại"}];
  public listVersion = [];
  constructor(private formBuilder: FormBuilder, private service: SoftwareLogService, public _translateService: TranslateService, private _changeLanguageService: ChangeLanguageService) { 
    this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
      this.currentLang = this._translateService.currentLang;
    })
  }

  ngOnInit(): void {
    this.initForm();
    
    
  }
  initForm(){
    this.addErrorLogForm = this.formBuilder.group(
      {
        error: ['',[Validators.required]],
        amendingcontent: ['',[Validators.required]],
        version: ['',[Validators.required]],
        note: ['',[Validators.required]],
        status: [null, [Validators.required]],
      },
    );
  }
  removeSpaces(control: AbstractControl) {
    if (control && control.value && !control.value.replace(/\s/g, '').length) {
      control.setValue('');
    }
    return null;
  }
  get AddErrorLogForm(){
    return this.addErrorLogForm.controls;
  }
  
  addError(){
    this.addErrorLogSubmitted = true;
    if(this.addErrorLogForm.value.error !== ''){
      this.addErrorLogForm.patchValue({
        error: this.addErrorLogForm.value.error.trim()
      })
    }
    if(this.addErrorLogForm.value.amendingcontent !== ''){
      this.addErrorLogForm.patchValue({
        amendingcontent: this.addErrorLogForm.value.amendingcontent.trim()
      })
    }
    if(this.addErrorLogForm.value.version !== ''){
      this.addErrorLogForm.patchValue({
        version: this.addErrorLogForm.value.version.trim()
      })
    }
    if(this.addErrorLogForm.value.note !== ''){
      this.addErrorLogForm.patchValue({
        note: this.addErrorLogForm.value.note.trim()
      })
    }
    if (this.addErrorLogForm.invalid) {
      return;
    }

    let content= this.addErrorLogForm.value;
    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
        .AddSoftwareLog(params)
        .then((data) =>{
          Swal.close();
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.SOFTWARE_LOG.ADD_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              // this.initForm();
              this.afterCreateSoftwareLog.emit('completed');
            });
          } else if(response.code === 3){
            Swal.fire({
              icon: "error",
              title: this._translateService.instant('MESSAGE.SOFTWARE_LOG.LOG_EXISTED'),
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

}
