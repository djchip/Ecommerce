import { Component, Input, OnInit } from '@angular/core';
import Swal from 'sweetalert2';
import { TranslateService } from '@ngx-translate/core';
import { ProofManagementService } from './../proof-management.service';
import { DatePipe } from '@angular/common';
import * as _ from 'lodash';

@Component({
  selector: 'app-detail-proof',
  templateUrl: './detail-proof.component.html',
  styleUrls: ['./detail-proof.component.scss']
})
export class DetailProofComponent implements OnInit {
  @Input() id;
  @Input() obj;
  @Input() objRevert;
  public dataRevert = {
    proofName: null,
    proofNameEn: null,
    proofCode: null,
    oldProofCode: null,
    standardId: null,
    criteriaId: null,
    documentType: null,
    numberSign: null,
    releaseDate: null,
    signer: null,
    field: null,
    releaseBy: null,
    description: null,
    descriptionEn: null,
    note: null,
    noteEn: null,
    createdBy: null,
    updatedBy: null,
    programId: null,
    orderOfStandard: null,
    orderOfCriteria: null,
    exhFile: null,
    uploadedDate: null,
    status: null,
    undoStatus: null,
    deleted: null,
    standarName:null,
    unit:null,
    createdDate: null,
    updatedDate: null,
  };

  public data: any;
  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public isHasFile = false;


  constructor(
    private datePipe: DatePipe,
    private service: ProofManagementService,
    public _translateService: TranslateService,
  ) { }

  ngOnInit(): void {
    if (this.id != null) {
      this.getProofDetail();
    }
    else {
      this.data = this.obj;
      if (this.objRevert !== null) {
        // console.log("obj, ", this.obj);
        // console.log("dtaa, ", this.data);
        this.compareObj();
      }
      else{
        this.formatPrograms(this.obj,1);
      }
    }
  }

  getProofDetail(){
    if(this.id !== ''){
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      this.service
        .detailProof(params, this.id)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
            console.log(data.file + '  tada')
            // if(data.file.length > 0){
            //   this.isHasFile = true;
            // }
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
    this.data.file.forEach(element => {
      this.service.download(element.id, element.fileName);
    });
  }
  objOrganizationId
  compareObj() {
    this.formatPrograms(this.obj,2);
    this.objOrganizationId = this.objRevert.standardId;
    this.obj.proofName === this.objRevert.proofName ? this.dataRevert.proofName = null : this.dataRevert.proofName = this.objRevert.proofName;
    this.obj.proofNameEn === this.objRevert.proofNameEn ? this.dataRevert.proofNameEn = null : this.dataRevert.proofNameEn = this.objRevert.proofNameEn;
    this.obj.proofCode === this.objRevert.proofCode ? this.dataRevert.proofCode = null : this.dataRevert.proofCode = this.objRevert.proofCode;
    this.obj.oldProofCode === this.objRevert.oldProofCode ? this.dataRevert.oldProofCode = null : this.dataRevert.oldProofCode = this.objRevert.oldProofCode;
    this.obj.standardId === this.objRevert.standardId ? this.dataRevert.standardId = null : this.dataRevert.standardId = this.objRevert.standardId;

    this.obj.criteriaId === this.objRevert.criteriaId ? this.dataRevert.criteriaId = null : this.dataRevert.criteriaId = this.objRevert.criteriaId;

    this.obj.documentType === this.objRevert.documentType ? this.dataRevert.documentType = null : this.dataRevert.documentType = this.objRevert.documentType;

    this.obj.numberSign === this.objRevert.numberSign ? this.dataRevert.numberSign = null : this.dataRevert.numberSign = this.objRevert.numberSign;

    this.obj.signer === this.objRevert.signer ? this.dataRevert.signer = null : this.dataRevert.signer = this.objRevert.signer;

    this.obj.field === this.objRevert.field ? this.dataRevert.field = null : this.dataRevert.field = this.objRevert.field;

    this.obj.releaseBy === this.objRevert.releaseBy ? this.dataRevert.releaseBy = null : this.dataRevert.releaseBy = this.objRevert.releaseBy;

    this.obj.description === this.objRevert.description ? this.dataRevert.description = null : this.dataRevert.description = this.objRevert.description;
    this.obj.descriptionEn === this.objRevert.descriptionEn ? this.dataRevert.descriptionEn = null : this.dataRevert.descriptionEn = this.objRevert.descriptionEn;

    this.obj.note === this.objRevert.note ? this.dataRevert.note = null : this.dataRevert.note = this.objRevert.note;

    this.obj.noteEn === this.objRevert.noteEn ? this.dataRevert.noteEn = null : this.dataRevert.noteEn = this.objRevert.noteEn;

    this.obj.createdBy === this.objRevert.createdBy ? this.dataRevert.createdBy = null : this.dataRevert.createdBy = this.objRevert.createdBy;

    this.obj.updatedBy === this.objRevert.updatedBy ? this.dataRevert.updatedBy = null : this.dataRevert.updatedBy = this.objRevert.updatedBy;

    this.obj.programId === this.objRevert.programId ? this.dataRevert.programId = null : this.dataRevert.programId = this.objRevert.programId;

    this.obj.orderOfStandard === this.objRevert.orderOfStandard ? this.dataRevert.orderOfStandard = null : this.dataRevert.orderOfStandard = this.objRevert.orderOfStandard;
    this.obj.orderOfCriteria === this.objRevert.orderOfCriteria ? this.dataRevert.orderOfCriteria = null : this.dataRevert.orderOfCriteria = this.objRevert.orderOfCriteria;

    this.obj.exhFile === this.objRevert.exhFile ? this.dataRevert.exhFile = null : this.dataRevert.exhFile = this.objRevert.exhFile;

    this.obj.uploadedDate === this.objRevert.uploadedDate ? this.dataRevert.uploadedDate = null : this.dataRevert.uploadedDate = this.objRevert.uploadedDate;

    this.obj.status === this.objRevert.status ? this.dataRevert.status = null : this.dataRevert.status = this.objRevert.status;
    this.obj.undoStatus === this.objRevert.undoStatus ? this.dataRevert.undoStatus = null : this.dataRevert.undoStatus = this.objRevert.undoStatus;

    this.obj.deleted === this.obj.deleted === this.objRevert.deleted ? this.dataRevert.deleted = null : this.dataRevert.deleted = this.objRevert.deleted;
    this.obj.unit === this.obj.unit === this.objRevert.unit ? this.dataRevert.unit = null : this.dataRevert.unit = this.objRevert.unit;
    this.obj.createdDate === this.objRevert.createdDate ? this.dataRevert.createdDate = null : this.dataRevert.createdDate = this.objRevert.createdDate;

    this.obj.updatedDate === this.objRevert.updatedDate ? this.dataRevert.updatedDate = null : this.dataRevert.updatedDate = this.objRevert.updatedDate;
    // this.obj.releaseDate === this.objRevert.releaseDate ? this.dataRevert.releaseDate = null : this.dataRevert.releaseDate = this.objRevert.releaseDate;
    this.formatPrograms(this.objRevert,1);
    
  }

  formatPrograms(obj, munberrr){
    obj.createdDate = obj.createdDate ? this.datePipe.transform(new Date(obj.createdDate), "dd-MM-yyyy HH:mm:ss") : null;
    obj.updatedDate = obj.updatedDate ? this.datePipe.transform(new Date(obj.updatedDate), "dd-MM-yyyy HH:mm:ss") : null;
    obj.releaseDate = obj.release ? this.datePipe.transform(new Date(obj.releaseDate), "dd-MM-yyyy") : null;
    // // obj.unit[0].createdDate = this.datePipe.transform(new Date(obj.unit[0]?.createdDate), "dd-MM-yyyy");
    // // obj.unit[0].updatedDate = this.datePipe.transform(new Date(obj.unit[0]?.updatedDate), "dd-MM-yyyy");
    var createdDate=obj.createdDate;
    var updatedDate= obj.updatedDate;
    var releaseDate=obj.releaseDate;
    if(obj.userInfos){
      obj.userInfos.array.forEach(element => {
        element.unit.createdDate = null;
        element.unit.updateDate = null;
        element.unit.releaseDate = null;  
      });
    }
    
    // var releaseDate = obj.releaseDate;


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
            // this.data.entity = response.content;
            // console.log("formatPrograms", this.data)
            if(munberrr ==1){
              this.data.entity = response.content;
    
            }else if(munberrr==2){
              this.obj = response.content;
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
