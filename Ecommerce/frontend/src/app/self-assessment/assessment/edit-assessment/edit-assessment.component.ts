import { ChangeLanguageService } from 'app/services/change-language.service';
import { TokenStorage } from 'app/services/token-storage.service';
import { TranslateService } from '@ngx-translate/core';
import { AssessmentService } from './../assessment.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-edit-assessment',
  templateUrl: './edit-assessment.component.html',
  styleUrls: ['./edit-assessment.component.scss']
})
export class EditAssessmentComponent implements OnInit {

  @Output() afterEditAssessment = new EventEmitter<String>();

  public editAssessmentForm: FormGroup;
  public editAssessmentFormSubmitted = false;
  public data;
  public id;
  public windowColla;
  public mergedPwdShow = false;
  public nameFile = '';
  public fileUpload: File;
  public onFile = false;
  mySwitch: boolean = false;
  public listPrograms = [];
  public listUser = [];
  public listViewers = [];
  public list = [];
  public userLoading = false;
  public idUser = 59;
  public maxSize = true;
  // public url = "https://10.252.10.236:9980/browser/a4b9c74/cool.html?WOPISrc=http://10.252.10.236:3232/neo/assessment/wopi/files/";
  urlSafe: SafeResourceUrl;
  public currentLang = this._translateService.currentLang;

  constructor(private formBuilder: FormBuilder, private service:AssessmentService, private _translateService:TranslateService,
    private tokenStorage: TokenStorage, private _changeLanguageService: ChangeLanguageService) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
      })
    }

  ngOnInit(): void {
    this.getListUser();
    this.getListViewers();
    this.initForm();
    this.id = window.localStorage.getItem("id");
    this.getAssessment();
  }

  initForm(){
    this.editAssessmentForm = this.formBuilder.group({
      id: [''],
      name: ['', [Validators.required]],
      nameEn: [''],
      programId: ['',[Validators.required]],
      user: [''],
      // viewers: [''],
      description: [''],
      descriptionEn: [''],
    })
  }

  fillForm(){
    this.editAssessmentForm.patchValue({
      id: this.data.id,
      name: this.data.name,
      nameEn: this.data.nameEn,
      programId: this.data.programId,
      user: this.data.user,
      // viewers: this.data.viewers,
      description: this.data.description,
      descriptionEn: this.data.descriptionEn,
    })
    this.nameFile = this.data.file;
  }

  getListPrograms(){
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getListPrograms(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listPrograms = data.content;
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

  getListUser(){
    let params = {
      method: "GET", roleId: this.idUser
    };
    this.service
      .getListUserByRole(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listUser = response.content;
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
          if(response.code === 2){
            this.listUser = [];
          }
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
  
  getListViewers(){
    let params = {
      method: "GET", keyword: "", roleId:-1, unitId:-1, perPage: 1000,
    };
    this.service
      .searchUser(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listViewers = response.content["items"];
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
          if(response.code === 2){
            this.listViewers = [];
          }
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

  onChange(){
    if(this.mySwitch){
      let xMax = window.outerWidth;
      let yMax = window.outerHeight;
      this.windowColla = window.open(this.service.getURLEditor(this.id), 'assessment', 'width='+xMax+', height='+yMax+',left=0,top=0');
    } else {
      this.windowColla.close();
    }
  }

  onFileChange(event){
    if(event.target.files.length > 0){
      const fileD = event.target.files[0];
      this.onFile = true;
      if(fileD.size > 52428800){
        this.maxSize = false;
      } else{
        this.maxSize = true;
      }
      console.log(fileD);
      
      this.fileUpload = event.target.files[0];
      this.data.file = this.fileUpload.name;
    }
  }

  editAssessment(){
    this.editAssessmentFormSubmitted = true;

    if(this.editAssessmentForm.invalid || !this.maxSize){
      return;
    }

    if(this.editAssessmentForm.value.name != ''){
      this.editAssessmentForm.patchValue({
        name: this.editAssessmentForm.value.name.trim()
      })
    }
    
    let arr = [];
    for(let [key, value] of Object.entries(this.editAssessmentForm.value.user)){
      arr.push(value['id']);
    }

    let arrViewers = [];
    // for(let [key, value] of Object.entries(this.editAssessmentForm.value.viewers)){
    //   arrViewers.push(value['id']);
    // }

    let content = this.editAssessmentForm.value;
    content.user = arr;
    // content.viewers = arrViewers;
    let params = {
      method: "PUT",
      content: content
    };
    Swal.showLoading();
    this.service
      .doSave(params, this.fileUpload)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.ASSESSMENT.UPDATE_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.afterEditAssessment.emit('completed');
          });
        }else if(response.code ===100){
          // console.log(100);
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.ASSESSMENT.NAME_EXIST'),
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

  get EditAssessmentForm(){
    return this.editAssessmentForm.controls;
  }

  async getAssessment(){
    if(this.id !== ''){
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      await this.service
        .getAssessmentById(params, this.id)
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
          // console.log("Detail", this.data);
          
        })
        .catch((error) => {
          Swal.close();
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          });
        });
        this.getListPrograms();
    }
  }

  resetForm(){
    this.fillForm();
    this.maxSize = true;
  }

}
