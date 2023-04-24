
import { ConfigService } from '../../config.service';
import { Component, Input, NgModule, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { TranslateService } from '@ngx-translate/core';
import { formatDate } from '@angular/common';
import { DatePipe } from '@angular/common';
@Component({
  selector: 'app-detail-exh-code',
  templateUrl: './detail-exh-code.component.html',
  styleUrls: ['./detail-exh-code.component.scss']
})
export class DetailExhCodeComponent implements OnInit {

  public contentHeader: object;
  public menuParentId;
  public data: any;
  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");

@Input () id;
@Input () obj;
@Input() objRevert;
public dataRevert = {
    id:null,
    name: null,
    nameEn: null,
    organizationId: null,
    createdBy: null,
    updatedBy: null,
    createdDate: null,
    updatedDate: null,
 };

  constructor(private datePipe: DatePipe,private router: Router, private service:ConfigService,public _translateService: TranslateService) {}

  ngOnInit(): void {
      this.contentHeader = {
          headerTitle: 'Thông tin người dùng',
          actionButton: true,
         
      };

      if( this.id != null){
        this.getMenuDetail();

      }else{
        this.data=this.obj;
        if(this.objRevert !== null){
          this.compareObj();
        }
        else{
            this.formatPrograms(this.obj);
        }
      }
     
    
    //   this.getListMenu();
  }



  menuParrentName: string
  getMenuDetail(){
      if (this.id !== ''){
          let params = {
              method: "GET"
          };
          Swal.showLoading();
          this.service
              .detailWWW(params, this.id)
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

  compareObj() {
    this.obj.id === this.objRevert.id ? this.dataRevert.id = null : this.dataRevert.id = this.objRevert.id;
    this.obj.name === this.objRevert.name ? this.dataRevert.name = null : this.dataRevert.name = this.objRevert.name;
    this.obj.nameEn === this.objRevert.nameEn ? this.dataRevert.nameEn = null : this.dataRevert.nameEn = this.objRevert.nameEn;
    this.obj.organizationId === this.objRevert.organizationId ? this.dataRevert.organizationId = null : this.dataRevert.organizationId = this.objRevert.organizationId;

    this.obj.createdBy === this.objRevert.createdBy ? this.dataRevert.createdBy = null : this.dataRevert.createdBy = this.objRevert.createdBy;
    this.obj.updatedBy === this.objRevert.updatedBy ? this.dataRevert.updatedBy = null : this.dataRevert.updatedBy = this.objRevert.updatedBy;
    this.obj.createdDate === this.objRevert.createdDate ? this.dataRevert.createdDate = null : this.dataRevert.createdDate = this.objRevert.createdDate;
    this.obj.updatedDate === this.objRevert.updatedDate ? this.dataRevert.updatedDate = null : this.dataRevert.updatedDate = this.objRevert.updatedDate;
   
  }

  formatPrograms(obj){
    obj.createdDate = this.datePipe.transform(new Date(obj.createdDate), "dd-MM-yyyy");
    obj.updatedDate = this.datePipe.transform(new Date(obj.updatedDate), "dd-MM-yyyy");
    // this.objOrganizationId = obj.organizationId;
    let params = {
      method: "POST",
      content: obj,
    };
    this.service
      .formatCode(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
            this.data = response.content;
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
        // Swal.fire({
        //   icon: "error",
        //   title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
        //   confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        // });
      });
  
  }

}
