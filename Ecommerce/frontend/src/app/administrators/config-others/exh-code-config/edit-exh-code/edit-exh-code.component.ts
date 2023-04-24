import { TranslateService } from '@ngx-translate/core';
import { ConfigService } from '../../config.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { NgbDateStruct, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ChangeLanguageService } from 'app/services/change-language.service';
import Swal from 'sweetalert2';
import { CriteriaService } from 'app/exhibition/criteria/criteria.service';

@Component({
  selector: 'app-edit-exh-code',
  templateUrl: './edit-exh-code.component.html',
  styleUrls: ['./edit-exh-code.component.scss']
})
export class EditExhCodeComponent implements OnInit {
  @Output() afterEditExhCode = new EventEmitter<string>();

  public currentLang = this._translateService.currentLang;
  public contentHeader: object;
  public editExhCodeForm: FormGroup;
  public editExhCodeFormSubmitted = false;
  public mergedPwdShow = false;
  public data;
  public id;
  public listOrg = [];
  public organizationId =null;

  constructor(
    private formBuilder: FormBuilder, 
    private serviceCriteria: CriteriaService,
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
    this.getExhCodeDetail();
    this.getListOrg();
  }

  getListOrg(){
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getListOrg(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listOrg = data.content;
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

  get EditExhCodeForm(){
    return this.editExhCodeForm.controls;
  }

  initForm(){
    this.editExhCodeForm = this.formBuilder.group(
      {
        id: [''],
        name: ['',[Validators.required]],
        organizationId: [null,[Validators.required]],
        enCode:['true',[Validators.required]],
      },
    );
  }

  async getExhCodeDetail(){
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

  editExhCode(){
    this.editExhCodeFormSubmitted = true;

    if(this.editExhCodeForm.invalid){
      return;
    }

    if(this.editExhCodeForm.value.name != ''){
      this.editExhCodeForm.patchValue({
        name: this.editExhCodeForm.value.name.trim()
      })
    }

    let content = this.editExhCodeForm.value;
    
    let params = {
      method: "PUT",
      content: content
    };
    Swal.showLoading();
    this.service
      .editExhCode(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('LABEL_APP_PARAM.UPDATE_EXHCODE_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.afterEditExhCode.emit('completed');
          });
        } else if(response.code ===100){
          console.log(100);
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('LABEL_APP_PARAM.EXHCODE_EXIST'),
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

  fillForm(){
    this.editExhCodeForm.patchValue(
      {
        id: this.data.id,
        name: this.data.name,
        organizationId: this.data.organizationId,
        enCode: String(this.data.enCode),
      },
    );
  }

}
