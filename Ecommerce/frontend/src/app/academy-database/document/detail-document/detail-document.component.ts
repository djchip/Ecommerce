import Swal from 'sweetalert2';
import { TranslateService } from '@ngx-translate/core';
import { DocumentService } from './../document.service';
import { Component, OnInit,Input } from '@angular/core';
import { ChangeLanguageService } from 'app/services/change-language.service';
import * as _ from 'lodash';
@Component({
  selector: 'app-detail-document',
  templateUrl: './detail-document.component.html',
  styleUrls: ['./detail-document.component.scss']
})
export class DetailDocumentComponent implements OnInit {

  @Input() id;
  @Input() obj;
  @Input() objRevert;
  public dataRevert = {
    id:null,
    documentNumber:null,
    documentName: null,
    documentNameEn: null,
    documentType: null,
    releaseDate:null,
    signer: null,
    field: null,
    releaseBy:null,
    description:null,
    descriptionEn:null,
    unit: null,
    attachments: null,
    createdDate:null,
    
   };
  public data:any;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public currentLang = this._translateService.currentLang;
  vn = "vn";
  en = "en";

  constructor(private service: DocumentService, 
    public _translateService: TranslateService,private _changeLanguageService: ChangeLanguageService) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
      console.log('lang' + this.currentLang == this.vn)
      });
    }

  ngOnInit(): void {
    // this.id = window.localStorage.getItem("id");
    if(this.id != null){
    this.getDocumentDetail();
    }
    else{
      this.data=this.obj;
      if( this.objRevert !== null){
        this.compareObj();

      }
    }

    console.log(" data = ", this.data);
  }
  
  getDocumentDetail(){
    if(this.id !== ''){
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      this.service
        .detailDocument(params, this.id)
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

  onDownloadFile(){
    console.log("GG " + this.data.file)
    this.data.file.forEach(element => {
      this.service.download(element.id, element.fileName);
    });
  }



  compareObj() {
    this.obj.id === this.objRevert.id ? this.dataRevert.id = null : this.dataRevert.id = this.objRevert.id;
    this.obj.documentNumber === this.objRevert.documentNumber ? this.dataRevert.documentNumber = null : this.dataRevert.documentNumber = this.objRevert.documentNumber;

    this.obj.documentName === this.objRevert.documentName ? this.dataRevert.documentName = null : this.dataRevert.documentName = this.objRevert.documentName;
    this.obj.documentNameEn === this.objRevert.documentNameEn ? this.dataRevert.documentNameEn = null : this.dataRevert.documentNameEn = this.objRevert.documentNameEn;

    this.obj.documentType === this.objRevert.documentType ? this.dataRevert.documentType = null : this.dataRevert.documentType = this.objRevert.documentType;
    this.obj.releaseDate === this.objRevert.releaseDate ? this.dataRevert.releaseDate = null : this.dataRevert.releaseDate = this.objRevert.releaseDate;
    this.obj.signer === this.objRevert.signer ? this.dataRevert.signer = null : this.dataRevert.signer = this.objRevert.signer;

    _.isEqual(this.obj.unit, this.objRevert.unit) ? this.dataRevert.unit = null : this.dataRevert.unit = this.objRevert.unit;

    this.obj.field === this.objRevert.field ? this.dataRevert.field = null : this.dataRevert.field = this.objRevert.field;

    this.obj.releaseBy === this.objRevert.releaseBy ? this.dataRevert.releaseBy = null : this.dataRevert.releaseBy = this.objRevert.releaseBy;
    this.obj.description === this.objRevert.description ? this.dataRevert.description = null : this.dataRevert.description = this.objRevert.description;
    this.obj.descriptionEn === this.objRevert.descriptionEn ? this.dataRevert.descriptionEn = null : this.dataRevert.descriptionEn = this.objRevert.descriptionEn;
    
    this.obj.attachments === this.objRevert.attachments ? this.dataRevert.attachments = null : this.dataRevert.attachments = this.objRevert.attachments;
    this.obj.createdDate === this.objRevert.createdDate ? this.dataRevert.createdDate = null : this.dataRevert.createdDate = this.objRevert.createdDate;

  }
}
