import { Component, OnInit,Input } from '@angular/core';
import Swal from 'sweetalert2';
import { Router } from "@angular/router";
import { CriteriaService } from '../criteria.service';
import { TranslateService } from '@ngx-translate/core';

import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-detail-criteria',
  templateUrl: './detail-criteria.html',
  styleUrls: ['./detail-criteria.scss']
})
export class DetaiCriteriaComponent implements OnInit {
  public currentLang = this._translateService.currentLang
  public contentHeader: object;
  public dateFormat = window.localStorage.getItem("dateFormat");
  // public id;
  
  public data: any;
  @Input() id;
  @Input() obj;
  @Input() objRevert;
  public dataRevert = {
      id:null,
      name: null,
      nameEn: null,
      code: null,
      description: null,
      descriptionEn: null,
      create_by: null,
      update_by: null,
      createdDate: null,
      updatedDate: null,
      organizationId: null,
      programId: null,
      orderCri: null,
      standarName: null,
      categoryId:null,
      categoryName:null,
      categoryNameEn:null,
    };

  constructor(private datePipe: DatePipe,private router: Router, private service: CriteriaService,  public _translateService: TranslateService) { }

  ngOnInit(): void {
    if(this.id != null){
      this.getDirectoryDetail();
    } else{
      
      this.data=this.obj;
      if(this.objRevert !== null){
        this.compareObj();
      }
      else{
        this.formatPrograms(this.obj,1);
    }
    }

    console.log(" data = ", this.data);
  }

  getDirectoryDetail(){
    if(this.id !== ''){
      let params = {
        method: "GET", lang: this._translateService.currentLang,
      };
      Swal.showLoading();
      this.service
        .detailDirectory(params, this.id)
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
  objOrganizationId
  compareObj() {
    this.formatPrograms(this.obj,2);
    this.objOrganizationId = this.objRevert.organizationId;
    this.obj.id === this.objRevert.id ? this.dataRevert.id = null : this.dataRevert.id = this.objRevert.id;

    this.obj.name === this.objRevert.name ? this.dataRevert.name = null : this.dataRevert.name = this.objRevert.name;
    this.obj.nameEn === this.objRevert.nameEn ? this.dataRevert.nameEn = null : this.dataRevert.nameEn = this.objRevert.nameEn;
    this.obj.code === this.objRevert.code ? this.dataRevert.code = null : this.dataRevert.code = this.objRevert.code;
    this.obj.description === this.objRevert.description ? this.dataRevert.description = null : this.dataRevert.description = this.objRevert.description;
    this.obj.descriptionEn === this.objRevert.descriptionEn ? this.dataRevert.descriptionEn = null : this.dataRevert.descriptionEn = this.objRevert.descriptionEn;

    this.obj.create_by === this.objRevert.create_by ? this.dataRevert.create_by = null : this.dataRevert.create_by = this.objRevert.create_by;
    this.obj.update_by === this.objRevert.update_by ? this.dataRevert.update_by = null : this.dataRevert.update_by = this.objRevert.update_by;
    this.obj.createdDate === this.objRevert.createdDate ? this.dataRevert.createdDate = null : this.dataRevert.createdDate = this.objRevert.createdDate;
    this.obj.updatedDate === this.objRevert.updatedDate ? this.dataRevert.updatedDate = null : this.dataRevert.updatedDate = this.objRevert.updatedDate;
    this.obj.categoryId === this.objRevert.categoryId ? this.dataRevert.categoryId = null : this.dataRevert.categoryId = this.objRevert.categoryId;
    this.obj.organizationId === this.objRevert.organizationId ? this.dataRevert.organizationId = null : this.dataRevert.organizationId = this.objRevert.organizationId;
    this.obj.programId === this.objRevert.programId ? this.dataRevert.programId = null : this.dataRevert.programId = this.objRevert.programId;
    this.obj.orderCri === this.objRevert.orderCri ? this.dataRevert.orderCri = null : this.dataRevert.orderCri = this.objRevert.orderCri;
    this.formatPrograms(this.objRevert,1);
  }

  
  formatPrograms(obj,numberr){
  
    obj.createdDate = obj.createdDate ? this.datePipe.transform(new Date(obj.createdDate), "yyyy-MM-dd HH:mm:ss") : null;
    obj.updatedDate = obj.updatedDate ? this.datePipe.transform(new Date(obj.updatedDate), "yyyy-MM-dd HH:mm:ss") : null;
 
      // obj.userInfos.forEach(element => {
      //   element.unit.createdDate =this.datePipe.transform(new Date(element.obj.createdDate), "dd-MM-yyyy")
      //   element.unit.updateDate = this.datePipe.transform(new Date(element.obj.updateDate), "dd-MM-yyyy")
      // });

      if(obj.userInfos){
        obj.userInfos.forEach(element => {
          element.unit.createdDate = null;
          element.unit.updatedDate = null;
        });
      }
    
    var createdDate=obj.createdDate;
    var updatedDate= obj.updatedDate;

    this.objOrganizationId = obj.organizationId;
    let params = {
      method: "POST",
      content: obj,
    };
    this.service
      .formatCode(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          if(numberr ==1){
            this.data = response.content;
            this.data.createdDate=createdDate;
            this.data.updatedDate=updatedDate;
          }
          else if(numberr ==2 ){
            this.obj=response.content;
            this.obj.createdDate=createdDate;
            this.obj.updatedDate=updatedDate;
            
          }
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
