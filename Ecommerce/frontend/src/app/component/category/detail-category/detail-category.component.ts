import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CoreTranslationService } from '@core/services/translation.service';
import { TranslateService } from '@ngx-translate/core';
import { CategoryService } from '../category.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-detail-category',
  templateUrl: './detail-category.component.html',
  styleUrls: ['./detail-category.component.scss']
})
export class DetailCategoryComponent implements OnInit {

  data;
  @Input() categoryId;
  public uploadFG: FormGroup;
  public currentLang = this._translateService.currentLang;
  public filenameUploaded = "";
  public pathFileArr = [];
  public changeFile = false;
  public fileUpload: File;
  public canSave = false;
  public file;

  constructor(
    private _changeLanguageService: ChangeLanguageService,
    private service: CategoryService,
    public _translateService: TranslateService,
    private _coreTranslationService: CoreTranslationService
  ) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
      
    });
  }

  ngOnInit(): void {
    this.getCategoryDetail();
  }

  async getCategoryDetail() {
    if (this.categoryId) {
      let params = {
        method: "GET",
      };
      Swal.showLoading();
      await this.service
        .detailCategory(params, this.categoryId)
        .then((data) => {
          let response = data;
          if (response.code == 0) {
            Swal.close();
            this.data = response.content;
          } else {
            Swal.fire({
              icon: "error",
              title: response.errorMessages,
            });
          }
        })
        .catch((error) => {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant(
              "MESSAGE.COMMON.CONNECT_FAIL"
            ),
            confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
          });
        });
    }
  }

  
  // fileNameUpload;
  // onFileChange(event) {
  //   this.canSave = true;
  //   if (event.target.files.length > 0) {
  //     this.fileUpload = event.target.files[0];
  //     this.fileNameUpload = document.getElementById('fileNameUpload');
  //     this.fileNameUpload.innerHTML  = this.fileUpload.name;
  //   }
  // }


  // dataSource
  // uploadForm() {
  //   debugger
  //   if(this.file == undefined && this.filenameUploaded != ""){
  //     Swal.showLoading();
  //     Swal.fire({
  //       icon: "success",
  //       title: this._translateService.instant('MESSAGE.FORM.UPDATE_DB_SUCCESS'),
  //       confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
  //     }).then((result) => {
  //       this.afterUpload.emit('completed');
  //     });
  //     return;
  //   }

  //   if(!this.canSave && this.filenameUploaded == ""){
  //     Swal.fire({
  //       icon: "error",
  //       title: this._translateService.instant('MESSAGE.FORM.FILE_DB_RQ'),
  //       confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
  //     }).then((result) => {
  //     });
  //     return;
  //   }
  //   Swal.showLoading();
  //   this.service.uploadForm({ 
  //     content: { 
  //       formName: this.data.name,
  //       year: this.data.yearOfApplication ? this.data.yearOfApplication + 0 : 0,
  //       numTitle: this.data.numTitle,
  //       isForm: false,
  //       formKey: this.data.formKey,
  //       startTitle: parseInt(this.data.startTitle),
  //     }}, this.fileUpload).then((data) => {
  //     Swal.close();
  //     if (data.body.code === 0) {
  //       this.dataSource = data.body.content;
  //       this.saveFileDatabase();
  //     }
  //   });
  // }

  // saveFileDatabase(){
  //   let content = {
  //     formKey: this.dataSource.formKey,
  //     pathFile: this.dataSource.pathFile
  //   };
  //   let params = {
  //     method: "PUT",
  //     content: content
  //   };
  //   Swal.showLoading();
  //   this.service.saveFileDatabase(params).then((data) => {
  //       Swal.close();
  //       let response = data;
  //       if (response.code === 0) {
  //         Swal.fire({
  //           icon: "success",
  //           title: this._translateService.instant('MESSAGE.FORM.UPDATE_DB_SUCCESS'),
  //           confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
  //         }).then((result) => {
  //           this.afterUpload.emit('completed');
  //         });
  //       } else {
  //         Swal.fire({
  //           icon: "error",
  //           title: response.errorMessages,
  //         });
  //       }
  //     }).catch((error) => {
  //       Swal.close();
  //       Swal.fire({
  //         icon: "error",
  //         title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
  //         confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
  //       });
  //     });
  // }

}
