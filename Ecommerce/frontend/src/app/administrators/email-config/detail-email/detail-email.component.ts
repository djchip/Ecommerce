import { CoreSidebarService } from '@core/components/core-sidebar/core-sidebar.service';
import { EmailService } from 'app/main/apps/email/email.service';
import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import { isNil, remove, reverse } from 'lodash';
import {ActivatedRoute, Router} from "@angular/router";
import {EmailConfigService} from "../email-config.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {CoreTranslationService} from "../../../../@core/services/translation.service";
import {TranslateService} from "@ngx-translate/core";
import Swal from "sweetalert2";
import {locale as eng} from "../../../../assets/languages/en";
import {locale as vie} from "../../../../assets/languages/vn";
import { ChangeLanguageService } from "app/services/change-language.service";
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import {
  DefaultTreeviewI18n, DownlineTreeviewItem,
  DropdownTreeviewComponent,
  OrderDownlineTreeviewEventParser,
  TreeviewEventParser, TreeviewI18n,
  TreeviewItem
} from "ngx-treeview";

@Component({
  selector: 'app-detail-email',
  templateUrl: './detail-email.component.html',
  styleUrls: ['./detail-email.component.scss'],
  encapsulation: ViewEncapsulation.None,
  host: { class: 'email-application' }
})
export class DetailEmailComponent implements OnInit {

  public contentHeader: object;
  public data: any;
  public email;
  public host;
  public port;
  public username;
  public items: TreeviewItem[]
  rows: string[];
  values: string[];
  public currentLang = this._translateService.currentLang
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;

  constructor(    private formBuilder: FormBuilder, 
    private _changeLanguageService: ChangeLanguageService,
      private router: Router,
      private service: EmailConfigService,
      private modalService: NgbModal,
      private _coreTranslationService: CoreTranslationService,
      public _translateService: TranslateService,
      private _emailService: EmailService,
      private _coreSidebarService: CoreSidebarService,
      private route: ActivatedRoute
  ) { 
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    });
  }


  ngOnInit(): void {
    this.privileges.forEach(element => {
      if(this.router.url === element.url){
        this.acceptAction = element.action;
      }
    });
    // content header
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.EMAIL_SERVER_CONFIG',
      actionButton: true,
      breadcrumb: {
        type: '',
        links: [
          {
            name: 'CONTENT_HEADER.MAIN_PAGE',
            isLink: true,
            link: '/dashboard'
          },
          {
            name: 'CONTENT_HEADER.ADMINISTRATOR',
            isLink: false,
            link: '/'
          },
          {
            name: 'CONTENT_HEADER.EMAIL_SERVER_CONFIG',
            isLink: false
          }
        ]
      }
    };
    this._coreTranslationService.translate(eng, vie);
    this.getListUser();
    this.getUserDetail();
    this.getMailTo();
    this.route.queryParams.subscribe(val => {
      this._emailService.updateSearchText(val.q);
    });
    // console.log(this.listEmailUser);
    
  }

  getUserDetail() {
    let params = {
      method: "GET"
    };
    Swal.showLoading();
    this.service
        .detailEmailConfig(params)
        .then((data) => {
          console.log("==========================DATA", data);
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.email = response.content.items[0].email;
            this.host = response.content.items[0].host;
            this.port = response.content.items[0].port;
            this.username = response.content.items[0].username;
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

  // modal Open Small
  openModalAddUser(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  afterEdit(){
    this.modalService.dismissAll();
    this.getUserDetail();
  }

  public openComposeRef;
  openCompose() {
    this.openComposeRef = true;
    this._emailService.composeEmail(this.openComposeRef);
    this._coreSidebarService.getSidebarRegistry('email-sidebar').toggleOpen();
  }

  toggleSidebar(nameRef): void {
    this._coreSidebarService.getSidebarRegistry(nameRef).toggleOpen();
  }

  public isComposeOpen: boolean = false;

  closeCompose() {
    this.isComposeOpen = false;
    this._emailService.composeEmail(this.isComposeOpen);
  }

  // Public
  public emailToSelect;

  public emailCCSelect;

  public quillEditorContent = '';
  public emailSubject = '';
  public emailToSelected = [];
  public emailToInput = '';
  public emailToInput1 = '';

  public emailInput = [];
  public listEmailUser=[];
  public ccSelected = [];
  public isOpenCC = false;
  public isOpenBCC = false;
  public listUser=[];
  public emailUser=[];
  public File: File;
  public addDocumentFormSubmitted = false;
  togglCcBcc(toggleRef) {
    if (toggleRef == 'cc') {
      this.isOpenCC = !this.isOpenCC;
    } else {
      this.isOpenBCC = !this.isOpenBCC;
    }
  }

  onChange(){
    console.log("Email: ", this.emailToInput);
    console.log("Email selectbox : ", this.emailToInput1);
    let splitted = this.emailToInput.split(";")
    let splitted1 = this.emailToInput1;
    console.log(splitted1);
    
    // console.log("Split: ", splitted);
    let mailformat = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
    if(splitted.length > 0 || splitted1.length > 0){
      this.emailInput = [];
      splitted.forEach((e) =>{
        if(e.trim().match(mailformat)){
          this.emailInput.push(e.trim());
          console.log("Email: ", this.emailInput);
        }
      })
      // splitted1.forEach((ee) =>{
       
          this.emailInput.push(splitted1);
          console.log("Email: ", this.listUser);
        
      // })

      
    }

   
    

    
  }
  onFileChange(event){
    if(event.target.files.length > 0){
      const fileD = event.target.files[0];
      this.File = event.target.files[0];
    }
  }


  send() {
    this.sendEmail();
    this.isComposeOpen = false;
    this._emailService.composeEmail(this.isComposeOpen);
  }
 

  sendEmail() {
    this.emailToSelected.forEach(element => this.convertDataEmailTo(element.email))
    this.ccSelected.forEach(element => this.convertDataCc(element.email))
  
    // return;
    // let params = {
    //   method: "GET",
    //   cc: this.ccSelected,
    //   subject: this.emailSubject,
    //   textContent: this.quillEditorContent,
    //   to: this.emailToSelected
    // };
    let params = {
      method: "POST",
      content: { 
        cc: this.mailCc,
        subject: this.emailSubject,
        textContent: this.quillEditorContent,
        atttachment: this.File,
        to: this.mailTo.concat(this.emailInput),
    
    },
    };
    console.log("params",params);
    Swal.showLoading();
    this.service
        .sendEmail(params, this.File)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.COMMON.SEND_EMAIL'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
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
            title: this._translateService.instant('MESSAGE.COMMON.ERROR_EMAIL'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          });
        });
  }

  getMailTo() {
    let params = {
      method: "GET"
    };
    Swal.showLoading();
    this.service
        .getMailTo(params)
        .then((data) => {
          Swal.close();
          let response = data;
          // debugger
          console.log("==========================data", data);
          if (response.code === 0) {
            this.emailToSelect = response.content;
            this.emailCCSelect = response.content;
            // console.log("==========================this.emailToSelect", this.emailToSelect);
            // console.log("==========================this.emailCCSelect", this.emailCCSelect);
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
  public mailTo = [];
  public mailCc = [];
  convertDataCc(item){
    this.mailCc.push(item);
  }

  convertDataEmailTo(item){
    this.mailTo.push(item);
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

          this.listUser.forEach((x) => {
            this.listEmailUser.push(x.email)
          })

          // this.data = response.content["items"];
          // this.listUser=this.data.email;

          // console.log(this.listEmailUser+" = email");
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


}
