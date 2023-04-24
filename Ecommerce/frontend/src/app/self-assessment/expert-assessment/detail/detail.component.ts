import { ChangeLanguageService } from 'app/services/change-language.service';
import { TokenStorage } from './../../../services/token-storage.service';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { ExpertService } from '../expert.service';
import { TranslateService } from '@ngx-translate/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import Swal from 'sweetalert2';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {
  @Output() afterEditAssessment = new EventEmitter<string>();
  public id;
  public data:any;
  mySwitch: boolean = true;
  public editAssessmentForm: FormGroup;
  // public url = "https://10.252.10.236:9980/browser/a4b9c74/cool.html?WOPISrc=http://10.252.10.236:3232/neo/assessment/wopi/files/";
  urlSafe: SafeResourceUrl;
  public listUser = [];
  public idUser = 59;
  public userLoading = false;
  public expertLoading=false;
  // public listEvaluate=null;
  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");
   public listEvaluate=[{id:0, name: "Chưa đánh giá", nameEn: "Not yet rated"}, 
                      {id:1, name: "Đã comment", nameEn: "Commented"}, 
                      {id:2, name: "Đã sửa", nameEn: "Fixed"},
                      {id:3, name: "Đạt", nameEn: "Achieve"},
                      {id:4, name: "Không đạt", nameEn: "Not achieved"}];
  constructor(private formBuilder: FormBuilder, private service:  ExpertService, public _translateService: TranslateService,private sanitizer: DomSanitizer, 
    private tokenStorage: TokenStorage, private _changeLanguageService: ChangeLanguageService) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
      })
    }

  ngOnInit(): void {
    this.initForm();
    this.getListUser();
    // this.fillForm();
    this.id = window.localStorage.getItem("id");
    this.getAssessmentDetail();
    // this.urlSafe = this.sanitizer.bypassSecurityTrustResourceUrl(this.url + this.id);
    // let xMax = window.outerWidth;
    // let yMax = window.outerHeight;
    // window.open(encodeURI(this.url + this.id + '?access_token=' + this.tokenStorage.getTokenStr()), 'assessment', 'width='+xMax+', height='+yMax+',left=0,top=0');
  }
  initForm(){
    this.editAssessmentForm = this.formBuilder.group({
      id: [''],
      evaluated: [''],
      comment:[''],
      listEvaluate:[''],
      // user: [''],
    })
  }
  fillForm(){
    this.editAssessmentForm.patchValue({
      id: this.id,
      evaluated: this.data.evaluated,
      comment: this.data.comment,
      listEvaluate:this.listEvaluate,
      // user: this.data.user,
    })
  }

  openAssessment(){
    let xMax = window.outerWidth;
    let yMax = window.outerHeight;
    window.open(this.service.getURLEditor(this.id), 'assessment', 'width='+xMax+', height='+yMax+',left=0,top=0');
  }

  get EditAssessmentForm(){
    return this.editAssessmentForm.controls;
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
  async getAssessmentDetail(){
    if(this.id !== ''){
      let params = {
        method: "GET", lang: this._translateService.currentLang,
      };
      
      Swal.showLoading();
      await this.service
        .detailAssessment(params, this.id)
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
        // this.listEvaluate=[{id:0, name: "Chưa đánh giá"}, {id:1, name: "Đã comment"}, {id:2, name: "đã sửa"},{id:"3", name: "đạt"},{id:"4", name: "không đạt"}];

    }
  }

  editExpert(){
    console.log(this.id);
    // let arr = [];
    // // console.log("USER", this.editAssessmentForm.value.user);
    // for(let [key, value] of Object.entries(this.editAssessmentForm.value.user)){
    //   arr.push(value['id']);
    // }
    let content = this.editAssessmentForm.value;
    // content.user = arr;
    console.log("content: ",content);
    
    let params = {
        method: "PUT",
        content: {id:this.id,evaluated:this.editAssessmentForm.value.evaluated, comment: this.editAssessmentForm.value.comment},
    };
    Swal.showLoading();
    this.service
        .doSave(params)
        .then((data) => {
            Swal.close();
            let response = data;
            if (response.code === 0) {
                Swal.fire({
                    icon: "success",
                    title: this._translateService.instant('MESSAGE.ASSESSMENT.EXPERT'),
                }).then((result) => {
                    this.afterEditAssessment.emit('completed');
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
