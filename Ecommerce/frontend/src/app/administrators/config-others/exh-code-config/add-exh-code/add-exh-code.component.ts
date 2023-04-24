import { TranslateService } from '@ngx-translate/core';
import { ConfigService } from '../../config.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { NgbDateStruct, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ChangeLanguageService } from 'app/services/change-language.service';
import Swal from 'sweetalert2';
import { CriteriaService } from 'app/exhibition/criteria/criteria.service';
import { OrganizationService } from 'app/exhibition/organization/organization.service';

@Component({
  selector: 'app-add-exh-code',
  templateUrl: './add-exh-code.component.html',
  styleUrls: ['./add-exh-code.component.scss']
})
export class AddExhCodeComponent implements OnInit {
  @Output() afterCreateExhCode = new EventEmitter<string>();

  public currentLang = this._translateService.currentLang;
  public contentHeader: object;
  public addExhCodeForm: FormGroup;
  public addExhCodeFormSubmitted = false;
  public mergedPwdShow = false;
  public data;
  public listOrg = [];
  public organizationId ="";
  public disableName = true;
  // public encode= true;


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
    this.getListOrg();
  }

  initForm(){
    this.addExhCodeForm = this.formBuilder.group(
      {
        name: ['',[Validators.required]],
        organizationId: [null,[Validators.required]],
        enCode:['true',[Validators.required]],
      },
    );
  }

//   onChangCodeOrganization(){
//     let organizationId = this.addExhCodeForm.value.organizationId;

//     if( this.encode == true){
//       this.organizationId= organizationId + "0";

//     }else if(this.encode == false){
//       this.organizationId= organizationId + "";

//     }
// console.log(" enCode ="+ this.encode)

//   }



  get AddExhCodeForm(){
    return this.addExhCodeForm.controls;
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

  addExhCode(){
    this.addExhCodeFormSubmitted = true;

    if(this.addExhCodeForm.invalid){
      return;
    }
    let content = this.addExhCodeForm.value;
    content.organizationId = this.addExhCodeForm.value.organizationId.id;
    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
        .addExhCode(params)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {

            // if( this.encode == true){
            //   this.organizationId= this.organizationId + "0";
            //   console.log(" enCodeSave1 ="+ this.encode)
        
            // }else if(this.encode ==false){
            //   this.organizationId= this.organizationId + "";
            //   console.log(" enCodeSave2 ="+ this.encode)
        
            // }

            Swal.fire({
              icon: "success",
              title: this._translateService.instant('LABEL_APP_PARAM.ADD_EXHCODE_SUCCESS'),
            }).then((result) => {
              this.afterCreateExhCode.emit('completed');
            });
            
          } else if(response.code === 3){
            Swal.fire({
              icon: "error",
              title: this._translateService.instant('LABEL_APP_PARAM.EXHCODE_EXIST'),
            }).then((result) => {
              this.afterCreateExhCode.emit('completed');
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
