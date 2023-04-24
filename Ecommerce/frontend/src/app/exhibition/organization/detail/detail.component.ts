import { Component, OnInit,Input } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { TranslateService } from '@ngx-translate/core';
import { OrganizationService } from '../organization.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { DatePipe } from '@angular/common';
@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {

  public contentHeader: object;
  public listMenu = [{id:"1", name: "Level 1"}, {id:"2", name: "Level 2"}, {id:"3", name: "Level 3"}];
  @Input() id;
  @Input() obj;
  @Input() objRevert;
  public dataRevert = {
      id:null,
      name: null,
      nameEn: null,
      categories: null,
      description: null,
      descriptionEn: null,
      createdBy: null,
      updatedBy: null,
      createdDate: null,
      updatedDate: null,
 
   };
  public menuParentId;
  public data: any;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public currentLang = this._translateService.currentLang;

  constructor(private datePipe: DatePipe,private _changeLanguageService: ChangeLanguageService,private router: Router, private service: OrganizationService,public _translateService: TranslateService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
      })
  }

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
   

      if (this.id != null && this.id != '') {
        this.getMenuDetail();
      }
      else {
        this.data = this.obj;
        if(this.objRevert !== null){
          this.compareObj();
        }
        else{
            this.formatPrograms(this.obj,1);
        }
      }
      console.log(" objRevert = ", this.objRevert);
      console.log(" obj = ", this.obj);


  }
  // getListMenu(){
  //     let params = {
  //         method: "GET",
  //     };
  //     Swal.showLoading();
  //     this.service
  //         .get(params)
  //         .then((data) => {
  //             Swal.close();
  //             let response = data;
  //             if (response.code === 0) {
  //                 this.listMenu = data.content;
  //                 console.log("menu",this.listMenu)
  //             } else {
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




  menuParrentName: string
  getMenuDetail(){
      if (this.id !== ''){
          let params = {
              method: "GET"
          };
          Swal.showLoading();
          this.service
              .detailUser(params, this.id)
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
    this.formatPrograms(this.obj,2)

    // debugger
    this.obj.id === this.objRevert.id ? this.dataRevert.id = null : this.dataRevert.id = this.objRevert.id;
    this.obj.name === this.objRevert.name ? this.dataRevert.name = null : this.dataRevert.name = this.objRevert.name;
    this.obj.nameEn === this.objRevert.nameEn ? this.dataRevert.nameEn = null : this.dataRevert.nameEn = this.objRevert.nameEn;
    this.obj.categories === this.objRevert.categories ? this.dataRevert.categories = null : this.dataRevert.categories = this.objRevert.categories;
    this.obj.description === this.objRevert.description ? this.dataRevert.description = null : this.dataRevert.description = this.objRevert.description;
    this.obj.descriptionEn === this.objRevert.descriptionEn ? this.dataRevert.descriptionEn = null : this.dataRevert.descriptionEn = this.objRevert.descriptionEn;
    this.obj.createdBy === this.objRevert.createdBy ? this.dataRevert.createdBy = null : this.dataRevert.createdBy = this.objRevert.createdBy;
    this.obj.updatedBy === this.objRevert.updatedBy ? this.dataRevert.updatedBy = null : this.dataRevert.updatedBy = this.objRevert.updatedBy;
    this.obj.createdDate === this.objRevert.createdDate ? this.dataRevert.createdDate = null : this.dataRevert.createdDate = this.objRevert.createdDate;
    this.obj.updatedDate === this.objRevert.updatedDate ? this.dataRevert.updatedDate = null : this.dataRevert.updatedDate = this.objRevert.updatedDate;
    this.formatPrograms(this.objRevert,1)
    
 }
 formatPrograms(obj, number){
  obj.createdDate = obj.createdDate ? this.datePipe.transform(new Date(obj.createdDate), "dd-MM-yyyy HH:mm:ss") : null;
  obj.updatedDate = obj.updatedDate ? this.datePipe.transform(new Date(obj.updatedDate), "dd-MM-yyyy HH:mm:ss") : null;
  var createdDate=obj.createdDate;
  var updatedDate= obj.updatedDate;
    let params = {
      method: "POST",
      content: obj,
    };
    this.service
      .formatCode(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          if(number ==1){
            this.data = response.content;
            // this.data.createdDate=createdDate;
            // this.data.updatedDate=updatedDate;
          }
          else if(number ==2 ){
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
        // Swal.fire({
        //   icon: "error",
        //   title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
        //   confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        // });
      });
  
  }

}
