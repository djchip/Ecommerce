import { ChangeLanguageService } from 'app/services/change-language.service';
import { CoreTranslationService } from './../../../../@core/services/translation.service';
import { SoftwareLogService } from "../software-log.service";
import { FormGroup, FormBuilder, Validators } from "@angular/forms";
import { FileUploader } from "ng2-file-upload";
import { Component, OnInit, ChangeDetectionStrategy, ViewEncapsulation, Output, EventEmitter } from "@angular/core";
import Swal from "sweetalert2";
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { TranslateService } from '@ngx-translate/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-import-software-log',
  templateUrl: './import-software-log.component.html',
  styleUrls: ['./import-software-log.component.scss'],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ImportSoftwareLogComponent implements OnInit {
  @Output() reloadFolder = new EventEmitter<string>();
  public uploader: FileUploader;
  public currentPage = 0;
  public currentLang = this._translateService.currentLang;
  constructor(private programService: SoftwareLogService,
    private formBuilder: FormBuilder,
    private modalService: NgbModal,
    public _translateService: TranslateService,
    private _coreTranslationService: CoreTranslationService,
    private _changeLanguageService: ChangeLanguageService,) {
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

  async import(i) {
    Swal.fire(this._translateService.instant('MESSAGE.COMMON.UPLOADING_DATA'))
    Swal.showLoading();
    var index = 0;
    this.uploader.queue.forEach(element => {
      if(index !== i){
        index++;
        return;
      }
      this.programService.import( element._file, { content: {  } }).then((data) => {
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
  }
  openModalAddMenus(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }
  afterCreateVersion(e, modal) {
    // this.modalService.dismissAll();
    if (e == "completed") {
        modal.close();
    }
    this.currentPage = 0
  }
  downloadMyFile(){
    const link = document.createElement('a');
    link.setAttribute('target', '_blank');
    link.setAttribute('href', 'assets/template-excel/Mẫu nhật ký lỗi phần mềm.xlsx');
    link.setAttribute('download', `Mẫu nhật ký lỗi phần mềm.xlsx`);
    document.body.appendChild(link);
    link.click();
    link.remove();
  }
}
