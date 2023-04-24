import { TranslateService } from '@ngx-translate/core';
import { ConfigService } from '../../config.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { NgbDateStruct, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ChangeLanguageService } from 'app/services/change-language.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-edit-field',
  templateUrl: './edit-field.component.html',
  styleUrls: ['./edit-field.component.scss']
})
export class EditFieldComponent implements OnInit {
  @Output() afterEditField = new EventEmitter<string>();

  public currentLang = this._translateService.currentLang;
  public contentHeader: object;
  public editFieldForm: FormGroup;
  public editFieldFormSubmitted = false;
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
      this.getFieldDetail();
    }
  
    initForm(){
      this.editFieldForm = this.formBuilder.group(
        {
          id: [''],
          name: ['',[Validators.required]],
          nameEn: [''],
        },
      );
    }

  get EditFieldForm(){
    return this.editFieldForm.controls;
  }

  fillForm(){
    this.editFieldForm.patchValue(
      {
        id: this.data.id,
        name: this.data.name,
        nameEn: this.data.nameEn,
      },
    );
  }

  async getFieldDetail(){
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

  editField(){
    this.editFieldFormSubmitted = true;

    if(this.editFieldForm.invalid){
      return;
    }

    if(this.editFieldForm.value.name != ''){
      this.editFieldForm.patchValue({
        name: this.editFieldForm.value.name.trim()
      })
    }

    let content = this.editFieldForm.value;
    
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
            title: this._translateService.instant('LABEL_APP_PARAM.UPDATE_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.afterEditField.emit('completed');
          });
        } else if(response.code ===100){
          console.log(100);
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('LABEL_APP_PARAM.DOCUMENT_TYPE_EXIST'),
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
