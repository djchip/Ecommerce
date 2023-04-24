import { TranslateService } from '@ngx-translate/core';
import { ConfigService } from '../../config.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { NgbDateStruct, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ChangeLanguageService } from 'app/services/change-language.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-add-document-type',
  templateUrl: './add-document-type.component.html',
  styleUrls: ['./add-document-type.component.scss']
})
export class AddDocumentTypeComponent implements OnInit {
  @Output() afterCreateDocumentType = new EventEmitter<string>();

  public currentLang = this._translateService.currentLang;
  public contentHeader: object;
  public addDocumentTypeForm: FormGroup;
  public addDocumentTypeFormSubmitted = false;
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
    this.addDocumentTypeForm = this.formBuilder.group(
      {
        name: ['',[Validators.required]],
        nameEn: [''],
      },
    );
  }

  get AddDocumentTypeForm(){
    return this.addDocumentTypeForm.controls;
  }

  addDocumentType(){
    this.addDocumentTypeFormSubmitted = true;

    if(this.addDocumentTypeForm.invalid){
      return;
    }
    let content = this.addDocumentTypeForm.value;
    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
        .addDocumentType(params)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('LABEL_APP_PARAM.ADD_SUCCESS'),
            }).then((result) => {
              this.afterCreateDocumentType.emit('completed');
            });
            
          } else if(response.code === 100){
            Swal.fire({
              icon: "error",
              title: this._translateService.instant('LABEL_APP_PARAM.DOCUMENT_TYPE_EXIST'),
            }).then((result) => {
              this.afterCreateDocumentType.emit('completed');
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
