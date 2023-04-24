import { Component, Input, OnInit, Directive } from '@angular/core';
import Swal from 'sweetalert2';
import { Router } from "@angular/router";
import { UserManagementService } from '../user-management.service';
import { TranslateService } from '@ngx-translate/core';
import * as _ from 'lodash';

@Component({
  selector: 'app-detail-user',
  templateUrl: './detail-user.component.html',
  styleUrls: ['./detail-user.component.scss']
})
export class DetailUserComponent implements OnInit {

  public contentHeader: object;
  public userId;
  public data: any;
  public dataRevert = {
    username: null,
    fullname: null,
    email: null,
    phoneNumber: null,
    role: null,
    unit: null,
    status: null
  };
  public rows = [];
  public tempData = this.rows;
  public currentLang = this._translateService.currentLang
  
  @Input() id;
  @Input() obj;
  @Input() objRevert;

  constructor(private router: Router, private service: UserManagementService,  public _translateService: TranslateService) { }

  ngOnInit(): void {

    if(this.id != null && this.id != ''){
      this.userId = this.id;
      this.getUserDetail();
    }else{
      this.userId = this.obj.id;
      this.data = this.obj;
      if(this.objRevert !== null){
        this.compareObj();
      }
      console.log("dataRevert ", this.dataRevert);
      console.log("obj ", this.obj);
      console.log("oobjRevertbj ", this.objRevert);


    }
  }

  getUserDetail(){
    if(this.userId !== ''){
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      this.service
        .detailUser(params, this.userId)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
            this.rows = response.content["items"];
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

  resetPassword(){
    Swal.fire({
      title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.CONFIRM_RESET_PASSWORD'),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      cancelButtonText: this._translateService.instant('ACTION.CANCEL'),
      customClass: {
        confirmButton: 'btn btn-primary',
        cancelButton: 'btn btn-danger ml-1'
      }
    }).then((result) => {
      if (result.value) {
        let params = {
          method: 'POST', content: { userId : this.userId}
        }
        this.service.resetPassword(params).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.RESET_PASSWORD_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
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
    });
  }

  compareObj() {
    this.obj.username === this.objRevert.username ? this.dataRevert.username = null : this.dataRevert.username = this.objRevert.username;
    this.obj.fullname === this.objRevert.fullname ? this.dataRevert.fullname = null : this.dataRevert.fullname = this.objRevert.fullname;
    this.obj.email === this.objRevert.email ? this.dataRevert.email = null : this.dataRevert.email = this.objRevert.email;
    this.obj.phoneNumber === this.objRevert.phoneNumber ? this.dataRevert.phoneNumber = null : this.dataRevert.phoneNumber = this.objRevert.phoneNumber;
    _.isEqual(this.obj.role, this.objRevert.role) ? this.dataRevert.role = null : this.dataRevert.role = this.objRevert.role;
    _.isEqual(this.obj.unit, this.objRevert.unit) ? this.dataRevert.unit = null : this.dataRevert.unit = this.objRevert.unit;
    this.obj.status === this.objRevert.status ? this.dataRevert.status = null : this.dataRevert.status = this.objRevert.status;
  }
}
