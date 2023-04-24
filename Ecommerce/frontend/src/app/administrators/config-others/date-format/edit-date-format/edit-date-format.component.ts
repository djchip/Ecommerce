import { TranslateService } from '@ngx-translate/core';
import { ConfigService } from '../../config.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { NgbDateStruct, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ChangeLanguageService } from 'app/services/change-language.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-edit-date-format',
  templateUrl: './edit-date-format.component.html',
  styleUrls: ['./edit-date-format.component.scss']
})
export class EditDateFormatComponent implements OnInit {

  @Output() afterEditDateFormat = new EventEmitter<string>();

  public currentLang = this._translateService.currentLang;
  public contentHeader: object;
  public editDateFormatForm: FormGroup;
  public editDateFormatFormSubmitted = false;
  public mergedPwdShow = false;
  public data;
  public id;

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
    this.id = window.localStorage.getItem("id");
    this.getDateFormatDetail();
  }

  initForm(){
    this.editDateFormatForm = this.formBuilder.group(
      {
        id: [''],
        name: ['',[Validators.required]],
      },
    );
  }

  get EditDateFormatForm(){
    return this.editDateFormatForm.controls;
  }

  fillForm(){
    this.editDateFormatForm.patchValue(
      {
        id: this.data.id,
        name: this.data.name,
      },
    );
  }

  async getDateFormatDetail(){
    if(this.id !== ''){
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      await this.service
        .detailDocumentType(params, this.id)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
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

  editDateFormat(){
    this.editDateFormatFormSubmitted = true;

    if(this.editDateFormatForm.invalid){
      return;
    }

    if(this.editDateFormatForm.value.name != ''){
      this.editDateFormatForm.patchValue({
        name: this.editDateFormatForm.value.name.trim()
      })
    }

    let content = this.editDateFormatForm.value;
    
    let params = {
      method: "PUT",
      content: content
    };
    Swal.showLoading();
    this.service
      .editDocumentType(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('LABEL_APP_PARAM.UPDATE_EXHCODE_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            localStorage.removeItem("dateFormat");
            localStorage.setItem("dateFormat", this.editDateFormatForm.value.name.trim())
            this.afterEditDateFormat.emit('completed');
          });
        } else if(response.code ===100){
          console.log(100);
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('LABEL_APP_PARAM.FORMAT_EXIST'),
          }).then((result) => {
          });
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
