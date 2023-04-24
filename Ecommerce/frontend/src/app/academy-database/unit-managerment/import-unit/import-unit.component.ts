import { ChangeLanguageService } from 'app/services/change-language.service';
import { CoreTranslationService } from './../../../../@core/services/translation.service';
import { UnitManagementService } from "./../unit-managerment.service";
import { FormGroup, FormBuilder, Validators } from "@angular/forms";
import { FileUploader } from "ng2-file-upload";
import { Component, OnInit, ChangeDetectionStrategy, ViewEncapsulation, Output, EventEmitter } from "@angular/core";
import Swal from "sweetalert2";
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { TranslateService } from '@ngx-translate/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: "app-import",
  templateUrl: "./import-unit.component.html",
  styleUrls: ["./import-unit.component.scss"],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ImportUnitComponent implements OnInit {
  public uploader: FileUploader; 
  public importFG: FormGroup;
  public currentLang = this._translateService.currentLang;
  ImportSubmitted = false;
  public currentPage = 0;
  public perPage = 10;
  public rows = [];
  public tempData = this.rows;
  public totalRows = 0;
  @Output() reloadFolder = new EventEmitter<string>();

  constructor(
    private UnitService: UnitManagementService,
    public _translateService: TranslateService,
    private _coreTranslationService: CoreTranslationService,
    private _changeLanguageService: ChangeLanguageService,
   
  ) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
      this.currentLang = this._translateService.currentLang;
    })
  }

  ngOnInit(): void {
    this.uploader = new FileUploader({
      isHTML5: true,
    });
    this._coreTranslationService.translate(eng, vie);
  }

 



  get ImportFG(){
    return this.importFG.controls;
  }
  async import(i) {
    Swal.fire({
      title: this._translateService.instant('MESSAGE.AUTO_UPLOAD_EXHIBITION.WARNING_OVERRIDE'),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      cancelButtonText: this._translateService.instant('ACTION.CANCEL'),
      customClass: {
        confirmButton: 'btn btn-primary',
        cancelButton: 'btn btn-danger ml-1'
      }
    }).then((result) => {
      if(result.value){
        // if(!this.validate()){
        //   return;
        // };
        Swal.fire(this._translateService.instant('MESSAGE.COMMON.UPLOADING_DATA'))
        Swal.showLoading();
        var index = 0;
        this.uploader.queue.forEach(element => {
          if(index !== i){
            index++;
            return;
          }
          this.UnitService.importUnit( element._file, { content: {  } }).then((data) => {
            Swal.close();
            if (data.body.code === 0) {
              element.isUploaded = true;
              element.isSuccess = true;
              element.progress = 100;
              Swal.fire({
                icon: 'success',
                title: this._translateService.instant('MESSAGE.AUTO_UPLOAD_EXHIBITION.SUCCESS_IMPORT'),
                confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
              })
            } else if(data.body.code == 7){
              Swal.fire(this._translateService.instant('MESSAGE.COMMON.NOT_EXCEL_FILE'));
            } else if(data.body.code == 3){
                  Swal.fire(this._translateService.instant('MESSAGE.DIRECTORY.ALREADY_EXITS'));
                  
            } else {
              element.isError = true;
              element.progress = 100;
            }
            if (element == this.uploader.queue[this.uploader.queue.length - 1]) {
              this.uploader.progress = 100;
              this.reloadFolder.emit();
            }
          }).catch(e => {
            Swal.fire(this._translateService.instant('MESSAGE.COMMON.SWW'));
          })
          index = 0;
        })
        this.ImportSubmitted = false;
      }
      });
  }

  // validate() :Boolean {
  //   this.ImportSubmitted = true;
  //   return this.importFG.invalid ? false : true;
  // }

  downloadMyFile(){
    const link = document.createElement('a');
    link.setAttribute('target', '_blank');
    link.setAttribute('href', 'assets/template-excel/Mau don vi.xlsx');
    link.setAttribute('download', `Mau don vi.xlsx`);
    document.body.appendChild(link);
    link.click();
    link.remove();
  }

  clear(){
    this.uploader.clearQueue();
  }
}
