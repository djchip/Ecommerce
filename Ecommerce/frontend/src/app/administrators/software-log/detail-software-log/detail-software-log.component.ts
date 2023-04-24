import { FormGroup } from '@angular/forms';
import { Component, OnInit, Input, EventEmitter,Output } from '@angular/core';
import Swal from 'sweetalert2';
import { Router } from "@angular/router";
import { SoftwareLogService } from '../software-log.service';
import { TranslateService } from '@ngx-translate/core';
import { ChangeLanguageService } from 'app/services/change-language.service';
@Component({
  selector: 'app-detail-software-log',
  templateUrl: './detail-software-log.component.html',
  styleUrls: ['./detail-software-log.component.scss']
})
export class DetailSoftwareLogComponent implements OnInit {
  public contentHeader: object;
  @Input () id;
  @Input() obj;
  @Input() objRevert;
  public dataRevert = {
    id:null,
    error: null,
    amendingcontent: null,
    version: null,
    note: null,
    create_by: null,
    create_date: null,
    updatedBy: null,
    successfulrevisiontime: null,
    status: null,
   };
   public dateFormat = window.localStorage.getItem("dateFormat");

  public data: any;
  // @Output() aftereditStatus = new EventEmitter<string>();
  // public editStatus: FormGroup;
  public currentLang = this._translateService.currentLang;
  public listStatus = [{id:"0", nameEn: "Bug", name: "Lỗi"}, 
                      {id:"1", nameEn: "Fixed", name: "Đã sửa"}, 
                      {id:"2", nameEn: "Closed", name: "Đã đóng"},
                      {id:"3", nameEn: "Cancel", name: "Làm lại"}];
  // public listStatus = [{id:"0", nameEn: "Bug", name: "L?i"}, {id:"1", nameEn: "Fixed", name: "�� s?a"}, {id:"2", nameEn: "Closed", name: "�� d�ng"},{id:"3", nameEn: "Cancel", name: "L�m l?i"}];
  constructor(private router: Router,private _changeLanguageService: ChangeLanguageService, private service: SoftwareLogService,  public _translateService: TranslateService) { 
    this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
      this.currentLang = this._translateService.currentLang;
    })
  }

  ngOnInit(): void {
    if(this.id != null){
      this.getSoftwareLogDetail();
    } else{
      this.data=this.obj;
      if(this.objRevert !== null){
        this.compareObj();
      }
    }
    console.log("dataRevert ", this.dataRevert);
  }
  getSoftwareLogDetail(){
    if(this.id !== ''){
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      this.service
        .detailSoftwareLog(params, this.id)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
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
//   editError(){
//     console.log(this.id);
//     let content = this.editStatus.value;
//     console.log("content: ",content);
    
//     let params = {
//         method: "PUT",
//         content: {id:this.id,evaluated:this.editStatus.value.evaluated,},
//     };
//     Swal.showLoading();
//     this.service
//         .editSoftwareLog(params)
//         .then((data) => {
//             Swal.close();
//             let response = data;
//             if (response.code === 0) {
//                 Swal.fire({
//                     icon: "success",
//                     title: this._translateService.instant('MESSAGE.ASSESSMENT.EXPERT'),
//                 }).then((result) => {
//                     this.aftereditStatus.emit('completed');
//                 });
//             }
//           else {
//                 Swal.fire({
//                     icon: "error",
//                     title: response.errorMessages,
//                 });
//             }
//         })
//         .catch((error) => {
//             Swal.close();
//             Swal.fire({
//                 icon: "error",
//                 title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
//                 confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
//             });
//         });

// }

compareObj() {
  
  this.obj.id === this.objRevert.id ? this.dataRevert.id = null : this.dataRevert.id = this.objRevert.id;
  this.obj.error === this.objRevert.error ? this.dataRevert.error = null : this.dataRevert.error = this.objRevert.error;
  this.obj.amendingcontent === this.objRevert.amendingcontent ? this.dataRevert.amendingcontent = null : this.dataRevert.amendingcontent = this.objRevert.amendingcontent;

  this.obj.version === this.objRevert.version ? this.dataRevert.version = null : this.dataRevert.version = this.objRevert.version;
  this.obj.note === this.objRevert.note ? this.dataRevert.note = null : this.dataRevert.note = this.objRevert.note;

  this.obj.create_by === this.objRevert.create_by ? this.dataRevert.create_by = null : this.dataRevert.create_by = this.objRevert.create_by;
  this.obj.create_date === this.objRevert.create_date ? this.dataRevert.create_date = null : this.dataRevert.create_date = this.objRevert.create_date;
  this.obj.updatedBy === this.objRevert.updatedBy ? this.dataRevert.updatedBy = null : this.dataRevert.updatedBy = this.objRevert.updatedBy;
  this.obj.successfulrevisiontime === this.objRevert.successfulrevisiontime ? this.dataRevert.successfulrevisiontime = null : this.dataRevert.successfulrevisiontime = this.objRevert.successfulrevisiontime;

}
}
