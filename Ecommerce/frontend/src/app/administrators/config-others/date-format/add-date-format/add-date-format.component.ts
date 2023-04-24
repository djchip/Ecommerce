import { TranslateService } from '@ngx-translate/core';
import { ConfigService } from '../../config.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { NgbDateStruct, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ChangeLanguageService } from 'app/services/change-language.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-add-date-format',
  templateUrl: './add-date-format.component.html',
  styleUrls: ['./add-date-format.component.scss']
})
export class AddDateFormatComponent implements OnInit {

  @Output() afterCreateDateFormat = new EventEmitter<string>();

  public currentLang = this._translateService.currentLang;
  public contentHeader: object;
  public addDateFormatForm: FormGroup;
  public addDateFormatFormSubmitted = false;
  public mergedPwdShow = false;
  public data;

  constructor(
    private formBuilder: FormBuilder, 
    private service: ConfigService, 
    public _translateService: TranslateService, 
    private modalService: NgbModal,
    private _changeLanguageService: ChangeLanguageService) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
      });
    }

  ngOnInit(): void {
    this.initForm();
  }

  initForm(){
    this.addDateFormatForm = this.formBuilder.group(
      {
        name: ['',[Validators.required]],
      },
    );
  }

  get AddDateFormatForm(){
    return this.addDateFormatForm.controls;
  }
  addDateFormat(){
    this.addDateFormatFormSubmitted = true;

    if(this.addDateFormatForm.invalid){
      return;
    }
    let content = this.addDateFormatForm.value;
    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
        .addDateFormat(params)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('LABEL_APP_PARAM.ADD_FORMAT_SUCCESS'),
            }).then((result) => {
              this.afterCreateDateFormat.emit('completed');
            });
            
          } else if(response.code ===3){
            Swal.fire({
              icon: "error",
              title: this._translateService.instant('LABEL_APP_PARAM.FORMAT_EXIST'),
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
