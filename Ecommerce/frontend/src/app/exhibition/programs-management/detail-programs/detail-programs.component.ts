import { Component, Input, NgModule, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { ProgramService } from '../programs.service';
import { TranslateService } from '@ngx-translate/core';
import * as _ from 'lodash';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-detail-programs',
  templateUrl: './detail-programs.component.html',
  styleUrls: ['./detail-programs.component.scss']
})
export class DetailProgramsComponent implements OnInit {


    @Input() id;
    @Input() obj;
    @Input() objRevert;
    public dataRevert = {
        id:null,
        name: null,
        createdDate: null,
        description: null,
        createdBy: null,
        updatedBy: null,
        updatedDate: null,
        note: null,
        organizationId: null,
        descriptionEn: null,
        nameEn: null,
        organizationName: null,
        organizationNameEn:null,
        categoryId:null,
        categoryName:null,
        categoryNameEn:null,
    
      };
  public contentHeader: object;
  public listMenu = [{id:"1", name: "Level 1"}, {id:"2", name: "Level 2"}, {id:"3", name: "Level 3"}];
 
  public menuParentId;
  public data: any;
  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");
  constructor(private router: Router, private service:ProgramService,public _translateService: TranslateService,
    private datePipe: DatePipe) {}

  ngOnInit(): void {
      this.contentHeader = {
          headerTitle: 'Thông tin người dùng',
          actionButton: true,
          breadcrumb: {
              type: '',
              links: [
                  {
                      name: 'Trang chủ',
                      isLink: true,
                      link: '/'
                  },
                  {
                      name: 'Quản trị hệ thống',
                      isLink: true,
                      link: '/'
                  },
                  {
                      name: 'Quản lý menu',
                      isLink: false
                  }
              ]
          }
      };
      if(this.id != null){
        this.getMenuDetail();
        this.getListMenu();
      }
      else {
        this.data = this.obj;
        if(this.objRevert !== null) {
            this.compareObj();
        }
        else{
            this.formatPrograms(this.obj, 1);
        }
      }
  }

  getListMenu(){
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
                  this.listMenu = data.content;
                  console.log("menu",this.listMenu)
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




  menuParrentName: string
  getMenuDetail(){
      if (this.id !== ''){
          let params = {
              method: "GET"
          };
          Swal.showLoading();
          this.service
              .detailProgram(params, this.id)
              .then((data) => {
                  Swal.close();
                  let response = data;
                  if (response.code === 0){
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
                      icon : "error",
                      title:"can not reach Gateway.",
                      confirmButtonText: "OK",
                  });
              });
      }
  }

  objOrganizationId
  compareObj() {
    
    this.formatPrograms(this.obj, 2);
    this.objOrganizationId = this.objRevert.organizationId;
    // obj côt resquets còn objRevert cột RevertObj
    // this.obj.id === this.objRevert.id ? this.dataRevert.id = null : this.dataRevert.id = this.objRevert.id;
    this.obj.name === this.objRevert.name ? this.dataRevert.name = null : this.dataRevert.name = this.objRevert.name;
    this.obj.createdDate === this.objRevert.createdDate ? this.dataRevert.createdDate = null : this.dataRevert.createdDate = this.objRevert.createdDate;
    this.obj.description === this.objRevert.description ? this.dataRevert.description = null : this.dataRevert.description = this.objRevert.description;
    this.obj.createdBy === this.objRevert.createdBy ? this.dataRevert.createdBy = null : this.dataRevert.createdBy = this.objRevert.createdBy;
    this.obj.updatedBy === this.objRevert.updatedBy ? this.dataRevert.updatedBy = null : this.dataRevert.updatedBy = this.objRevert.updatedBy;
    this.obj.updatedDate === this.objRevert.updatedDate ? this.dataRevert.updatedDate = null : this.dataRevert.updatedDate = this.objRevert.updatedDate;
    this.obj.note === this.objRevert.note ? this.dataRevert.note = null : this.dataRevert.note = this.objRevert.note;
    this.obj.organizationId === this.objRevert.organizationId ? this.dataRevert.organizationId = null : this.dataRevert.organizationId = this.objRevert.organizationId;
    this.obj.descriptionEn === this.objRevert.descriptionEn ? this.dataRevert.descriptionEn = null : this.dataRevert.descriptionEn = this.objRevert.descriptionEn;
    this.obj.nameEn === this.objRevert.nameEn ? this.dataRevert.nameEn = null : this.dataRevert.nameEn = this.objRevert.nameEn;
    this.dataRevert.organizationName == this.obj.organizationName;
    this.dataRevert.organizationNameEn == this.obj.organizationNameEn;
    this.dataRevert.categoryName == this.obj.categoryName;
    this.dataRevert.categoryNameEn == this.obj.categoryNameEn;
    this.obj.categoryId === this.objRevert.categoryId ? this.dataRevert.categoryId = null : this.dataRevert.categoryId = this.objRevert.categoryId;

    this.formatPrograms(this.objRevert, 1);

  }

  formatPrograms(obj, caseFormat){
    obj.createdDate = obj.createdDate ? this.datePipe.transform(new Date(obj.createdDate),"yyyy-MM-dd HH:mm:ss"):null;
    obj.updatedDate = obj.updatedDate? this.datePipe.transform(new Date(obj.updatedDate),  "yyyy-MM-dd HH:mm:ss") : null;

    var createdDate=obj.createdDate;
    var updatedDate= obj.updatedDate;
    // obj.organizationId = this.obj.organizationId;
    this.objOrganizationId = obj.organizationId;

    let params = {
      method: "POST",
      content: obj,
    };
    this.service
      .formatPrograms(params)
      .then((data) => {
        
        let response = data;
        if (response.code === 0) {
            if(caseFormat ==1){
                this.data = response.content;
                this.data.createdDate=createdDate;
                this.data.updatedDate=updatedDate;
              }
              else if(caseFormat ==2 ){
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

