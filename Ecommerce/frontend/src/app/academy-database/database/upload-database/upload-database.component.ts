import { formatDate } from "@angular/common";
import { CoreTranslationService } from "./../../../../@core/services/translation.service";
import { TranslateService } from "@ngx-translate/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { FormService } from "./../../form-management/form.service";
import { ChangeLanguageService } from "./../../../services/change-language.service";
import { Component, OnInit, Output, EventEmitter, Input } from "@angular/core";
import { locale as eng } from "assets/languages/en";
import { locale as vie } from "assets/languages/vn";
import Swal from "sweetalert2";
import { log } from "console";

@Component({
  selector: "app-upload-database",
  templateUrl: "./upload-database.component.html",
  styleUrls: ["./upload-database.component.scss"],
})
export class UploadDatabaseComponent implements OnInit {
  data;
  @Output() afterUpload = new EventEmitter<string>();
  @Input() copyId;
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
    private service: FormService,
    public _translateService: TranslateService,
    private _coreTranslationService: CoreTranslationService
  ) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
      
    });
  }

  ngOnInit(): void {
    this._coreTranslationService.translate(eng, vie);
    this.getFormDetail();
  }

  async getFormDetail() {
    if (this.copyId) {
      let params = {
        method: "GET",
      };
      Swal.showLoading();
      await this.service
        .detailFormDB(params, this.copyId)
        .then((data) => {
          let response = data;
          if (response.code == 0) {
            Swal.close();
            this.data = response.content;
            if(this.data.pathFileDatabase != null) {
              this.pathFileArr = this.data.pathFileDatabase.split("/");
              this.filenameUploaded = this.pathFileArr[this.pathFileArr.length - 1];
            }
            
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

  
  fileNameUpload;
  onFileChange(event) {
    this.canSave = true;
    if (event.target.files.length > 0) {
      this.fileUpload = event.target.files[0];
      this.fileNameUpload = document.getElementById('fileNameUpload');
      this.fileNameUpload.innerHTML  = this.fileUpload.name;
    }
  }

  openDatabase(){
    let xMax = window.outerWidth;
    let yMax = window.outerHeight;
    window.open( this.service.getURLEditor(this.copyId), 'assessment', 'width='+xMax+', height='+yMax+',left=0,top=0');
  }

  dataSource
  uploadForm() {
    debugger
    if(this.file == undefined && this.filenameUploaded != ""){
      Swal.showLoading();
      Swal.fire({
        icon: "success",
        title: this._translateService.instant('MESSAGE.FORM.UPDATE_DB_SUCCESS'),
        confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      }).then((result) => {
        this.afterUpload.emit('completed');
      });
      return;
    }

    if(!this.canSave && this.filenameUploaded == ""){
      Swal.fire({
        icon: "error",
        title: this._translateService.instant('MESSAGE.FORM.FILE_DB_RQ'),
        confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      }).then((result) => {
      });
      return;
    }
    Swal.showLoading();
    this.service.uploadForm({ 
      content: { 
        formName: this.data.name,
        year: this.data.yearOfApplication ? this.data.yearOfApplication + 0 : 0,
        numTitle: this.data.numTitle,
        isForm: false,
        formKey: this.data.formKey,
        startTitle: parseInt(this.data.startTitle),
      }}, this.fileUpload).then((data) => {
      Swal.close();
      if (data.body.code === 0) {
        this.dataSource = data.body.content;
        this.saveFileDatabase();
      }
    });
  }

  saveFileDatabase(){
    let content = {
      formKey: this.dataSource.formKey,
      pathFile: this.dataSource.pathFile
    };
    let params = {
      method: "PUT",
      content: content
    };
    Swal.showLoading();
    this.service.saveFileDatabase(params).then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.FORM.UPDATE_DB_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.afterUpload.emit('completed');
          });
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
      }).catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        });
      });
  }
}
