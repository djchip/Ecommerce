import { TokenStorage } from 'app/services/token-storage.service';
import { TranslateService } from '@ngx-translate/core';
import { AssessmentService } from './../assessment.service';
import { Component, OnInit, Input } from '@angular/core';
import Swal from 'sweetalert2';
import { ChangeLanguageService } from 'app/services/change-language.service';


@Component({
  selector: 'app-detail-assessment',
  templateUrl: './detail-assessment.component.html',
  styleUrls: ['./detail-assessment.component.scss']
})
export class DetailAssessmentComponent implements OnInit {
  public dateFormat = window.localStorage.getItem("dateFormat");
  public currentLang = this._translateService.currentLang;
  public roleAdmin = window.localStorage.getItem("ADM");

  @Input() id;
  @Input() obj;
  @Input() objRevert;
  public dataRevert = {
    id: null,
    name: null,
    nameEn: null,
    description: null,
    descriptionEn: null,
    file: null,
    createdBy: null,
    updatedBy: null,
    evaluated: null,
    programId: null,
    temp: null,
    orderAss: null,
    content: null,
    createdDate: null,
    updatedDate: null,

  };
  public data: any;
  public currentUserName;


  constructor(private _changeLanguageService: ChangeLanguageService, private service: AssessmentService, public _translateService: TranslateService, 
    private tokenStorage: TokenStorage) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    })
  }

  ngOnInit(): void {
    this.currentUserName = this.tokenStorage.getUsername();
    if (this.id != null && this.id != '') {
      this.getAssessmentDetail();
    }
    else {
      this.data = this.obj;
      if (this.objRevert !== null) {
        this.compareObj();
      }
    }
    console.log("data", this.data)

  }

  getAssessmentDetail() {
    if (this.id !== '') {
      let params = {
        method: "GET", lang: this._translateService.currentLang,
      };
      Swal.showLoading();
      this.service
        .detailAssessment(params, this.id)
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

  onDownloadFile() {
    this.service.download(this.id, this.data.file);
  }

  compareObj() {
    this.obj.id === this.objRevert.id ? this.dataRevert.id = null : this.dataRevert.id = this.objRevert.id;
    this.obj.name === this.objRevert.name ? this.dataRevert.name = null : this.dataRevert.name = this.objRevert.name;
    this.obj.nameEn === this.objRevert.nameEn ? this.dataRevert.nameEn = null : this.dataRevert.nameEn = this.objRevert.nameEn;
    this.obj.description === this.objRevert.description ? this.dataRevert.description = null : this.dataRevert.description = this.objRevert.description;
    this.obj.descriptionEn === this.objRevert.descriptionEn ? this.dataRevert.descriptionEn = null : this.dataRevert.descriptionEn = this.objRevert.descriptionEn;
    this.obj.file === this.objRevert.file ? this.dataRevert.file = null : this.dataRevert.file = this.objRevert.file;

    this.obj.createdBy === this.objRevert.createdBy ? this.dataRevert.createdBy = null : this.dataRevert.createdBy = this.objRevert.createdBy;
    this.obj.updatedBy === this.objRevert.updatedBy ? this.dataRevert.updatedBy = null : this.dataRevert.updatedBy = this.objRevert.updatedBy;
   
    this.obj.evaluated === this.objRevert.evaluated ? this.dataRevert.evaluated = null : this.dataRevert.evaluated = this.objRevert.evaluated;
    this.obj.programId === this.objRevert.programId ? this.dataRevert.programId = null : this.dataRevert.programId = this.objRevert.programId;
    this.obj.temp === this.objRevert.temp ? this.dataRevert.temp = null : this.dataRevert.temp = this.objRevert.temp;
    this.obj.orderAss === this.objRevert.orderAss ? this.dataRevert.orderAss = null : this.dataRevert.orderAss = this.objRevert.orderAss;
    this.obj.content === this.objRevert.content ? this.dataRevert.content = null : this.dataRevert.content = this.objRevert.content;

    this.obj.createdDate === this.objRevert.createdDate ? this.dataRevert.createdDate = null : this.dataRevert.createdDate = this.objRevert.createdDate;
    this.obj.updatedDate === this.objRevert.updatedDate ? this.dataRevert.updatedDate = null : this.dataRevert.updatedDate = this.objRevert.updatedDate;
  }

}
