import { ChangeLanguageService } from 'app/services/change-language.service';
import { Component, Input, OnInit } from '@angular/core';
import Swal from 'sweetalert2';
import { Router } from "@angular/router";
import { UnitManagementService } from '../unit-managerment.service';
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-detail-unit',
  templateUrl: './detail-unit.component.html',
  styleUrls: ['./detail-unit.component.scss']
})
export class DetailUnitComponent implements OnInit {

  public contentHeader: object;
  // public unitId;
  public data: any;
  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public listClassify = [{id: 1, name: "Văn phòng học viện", nameEn: "Academy office"},
                        {id: 2, name: "Khoa", nameEn: "Faculty"},
                        {id: 3, name: "Viện", nameEn: "Institute"},
                        {id: 4, name: "Trung tâm", nameEn: "Center"},
                        {id: 5, name: "Công ty", nameEn: "Company"}];
  @Input() id;
  @Input() obj;
  @Input() objRevert;
  public dataRevert = {
    id:null,
    unitName: null,
    unitNameEn: null,
    unitCode: null,
    description: null,
    descriptionEn: null,
    createdBy: null,
    updatedBy: null,
    email: null,
    classify: null,
    createdDate: null,
    updatedDate: null,
  
 };


  constructor(private datePipe: DatePipe,private router: Router, private service: UnitManagementService,  public _translateService: TranslateService, 
    private _changeLanguageService: ChangeLanguageService) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
      })
    }

  ngOnInit(): void {
    if(this.id != null){
    this.getUnitDetail();}
    else{
      this.data=this.obj;
      if(this.objRevert !== null){
        this.compareObj();
      }
    }
    console.log("data", this.data)
    console.log("obj", this.obj)

  }

  getUnitDetail(){
    if(this.id !== ''){
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      this.service
        .detailUnit(params, this.id)
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
  compareObj() {
    this.obj.id === this.objRevert.id ? this.dataRevert.id = null : this.dataRevert.id = this.objRevert.id;
    this.obj.unitName === this.objRevert.unitName ? this.dataRevert.unitName = null : this.dataRevert.unitName = this.objRevert.unitName;
    this.obj.unitNameEn === this.objRevert.unitNameEn ? this.dataRevert.unitNameEn = null : this.dataRevert.unitNameEn = this.objRevert.unitNameEn;
    this.obj.unitCode === this.objRevert.unitCode ? this.dataRevert.unitCode = null : this.dataRevert.unitCode = this.objRevert.unitCode;
    this.obj.description === this.objRevert.description ? this.dataRevert.description = null : this.dataRevert.description = this.objRevert.description;

    this.obj.descriptionEn === this.objRevert.descriptionEn ? this.dataRevert.descriptionEn = null : this.dataRevert.descriptionEn = this.objRevert.descriptionEn;
    this.obj.createdBy === this.objRevert.createdBy ? this.dataRevert.createdBy = null : this.dataRevert.createdBy = this.objRevert.createdBy;
    this.obj.updatedBy === this.objRevert.updatedBy ? this.dataRevert.updatedBy = null : this.dataRevert.updatedBy = this.objRevert.updatedBy;
    this.obj.email === this.objRevert.email ? this.dataRevert.email = null : this.dataRevert.email = this.objRevert.email;
    this.obj.classify === this.objRevert.classify ? this.dataRevert.classify = null : this.dataRevert.classify = this.objRevert.classify;
    this.obj.createdDate === this.objRevert.createdDate ? this.dataRevert.createdDate = null : this.dataRevert.createdDate = this.objRevert.createdDate;
    this.obj.updatedDate === this.objRevert.updatedDate ? this.dataRevert.updatedDate = null : this.dataRevert.updatedDate = this.objRevert.updatedDate;
   
   
  }

  formratdate(obj){
    obj.createdDate = this.datePipe.transform(new Date(obj.createdDate), "dd-MM-yyyy HH:mm:ss");
    obj.updatedDate = this.datePipe.transform(new Date(obj.updatedDate), "dd-MM-yyyy HH:mm:ss");
    // this.objOrganizationId = obj.organizationId;
  }
}
