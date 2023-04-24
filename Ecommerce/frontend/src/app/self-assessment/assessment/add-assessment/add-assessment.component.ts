import { ChangeLanguageService } from 'app/services/change-language.service';
import { TokenStorage } from 'app/services/token-storage.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';
import { AssessmentService } from './../assessment.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-add-assessment',
  templateUrl: './add-assessment.component.html',
  styleUrls: ['./add-assessment.component.scss']
})
export class AddAssessmentComponent implements OnInit {

  @Output() afterCreateAssessment = new EventEmitter<string>();

  public addAssessmentForm: FormGroup;
  public addAssessmentFormSubmitted = false;
  public mergedPwdShow = false;
  public data;
  public windowColla;
  public fileUpload: File;
  public onFile = false;
  public listPrograms = [];
  public listUserExpert = [];
  public listUser = [];
  public userLoading = false;
  public viewersLoading = false;
  public idUser = 59;
  public idA;
  public maxSize = true;
  // public url = "https://10.252.10.236:9980/browser/a4b9c74/cool.html?WOPISrc=http://10.252.10.236:3232/neo/assessment/wopi/files/";
  urlSafe: SafeResourceUrl;
  mySwitch: boolean = false;
  public currentLang = this._translateService.currentLang;

  constructor(private formBuilder: FormBuilder, private service: AssessmentService, public _translateService: TranslateService,
    private tokenStorage: TokenStorage, private _changeLanguageService:ChangeLanguageService) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
      })
    }

  ngOnInit(): void {
    this.initForm();
    this.getListPrograms();
    this.getListUserExpert();
    this.getListUser();
  }

  initForm(){
    this.addAssessmentForm = this.formBuilder.group({
      id: [''],
      name: ['', [Validators.required]],
      nameEn: [''],
      programId: [null,[Validators.required]],
      user: [''],
      viewers: [''],
      description: [''],
      descriptionEn: [''],
      reportType:[3],
    })
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

  getListUserExpert(){
    let params = {
      method: "GET", roleId: this.idUser
    };
    this.service
      .getListUserByRole(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listUserExpert = response.content;
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
          if(response.code === 2){
            this.listUserExpert = [];
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

  getListUser(){
    let params = {
      method: "GET", keyword: "", roleId:-1, unitId:-1, perPage: 1000,
    };
    this.service
      .searchUser(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listUser = response.content["items"];
          console.log(this.listUser);
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

  onChange(){
    if(this.mySwitch){
      this.idA = window.localStorage.getItem("idA");
      let xMax = window.outerWidth;
      let yMax = window.outerHeight;
      this.windowColla = window.open(this.service.getURLEditor(this.idA), 'assessment', 'width='+xMax+', height='+yMax+',left=0,top=0');
    } else {
      this.windowColla.close();
    }
  }

  onFileChange(event){
    if(event.target.files.length > 0){
      this.onFile = true;
      const fileD = event.target.files[0];
      if(fileD.size > 52428800){
        this.maxSize = false;
      } else{
        this.maxSize = true;
      }
      console.log(fileD);
      this.fileUpload = event.target.files[0];
    }
  }

  addAssessment(){
    this.addAssessmentFormSubmitted =true;

    if (this.addAssessmentForm.value.name !== '') {
      this.addAssessmentForm.patchValue({
        name: this.addAssessmentForm.value.name.trim()
      })
    }

    if(this.addAssessmentForm.invalid){
      return;
    }
    if(!this.onFile && !this.mySwitch){
      Swal.fire({
        icon: "warning",
        title: this._translateService.instant('MESSAGE.ASSESSMENT.FILE_REQUIRE'),
      })
      return;
    }

    if(!this.maxSize){
      return;
    }
    let content = this.addAssessmentForm.value;
    content.id = window.localStorage.getItem("idA");
    content.programId = this.addAssessmentForm.value.programId.id;
    console.log(this.addAssessmentForm.value.user);
    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
        .doSave(params, this.fileUpload)
        .then((data) => {
        Swal.close();
        let response = data;
        // console.log(response.code);
        
        if (response.code === 0) {
          // console.log(0);
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.ASSESSMENT.ADD_SUCCESS'),
          }).then((result) => {
            this.afterCreateAssessment.emit('completed');
          });
        } else if(response.code ===100){
          // console.log(100);
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.ASSESSMENT.NAME_EXIST'),
          }).then((result) => {
          });
        } else {
          console.log("err");
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
      })
      .catch((error) => {
        console.log("catch");
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        });
      });
  }

  get AddAssessmentForm(){
    return this.addAssessmentForm.controls;
  }

  onClick(){
    this.maxSize = true;
  }

}
