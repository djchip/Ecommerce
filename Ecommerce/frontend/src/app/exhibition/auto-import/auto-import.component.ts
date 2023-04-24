import { UploadFormWizardComponent } from "./../upload-form-wizard/upload-form-wizard.component";
import { AutoImportService } from "./auto-import.service";
import {
  Component,
  OnInit,
  Input,
  Output,
  EventEmitter,
  Host,
  ViewEncapsulation,
} from "@angular/core";
import { FileUploader } from "ng2-file-upload";
import { TranslateService } from "@ngx-translate/core";
import { CoreTranslationService } from "@core/services/translation.service";
import { locale as eng } from "assets/languages/en";
import { locale as vie } from "assets/languages/vn";
import Swal from "sweetalert2";
import { ColumnMode } from "@swimlane/ngx-datatable";
import { ChangeLanguageService } from 'app/services/change-language.service';
@Component({
  selector: "app-auto-import",
  templateUrl: "./auto-import.component.html",
  styleUrls: ["./auto-import.component.scss"],
  encapsulation: ViewEncapsulation.None,
})
export class AutoImportComponent implements OnInit {
  public uploader: FileUploader;
  public messageNotFound: "Không tìm thấy bản ghi nào !";
  public contentHeader: object;
  @Input() organizationId;
  @Input() programId;
  @Input() forWhat;
  @Output() reloadFolder = new EventEmitter<string>();
  parent: UploadFormWizardComponent;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public totalRows = 0;
  public messages;
  public currentLang = this._translateService.currentLang;

  constructor(
    private _changeLanguageService: ChangeLanguageService,
    private service: AutoImportService,
    private _coreTranslationService: CoreTranslationService,
    public _translateService: TranslateService,
    @Host() currentParent: UploadFormWizardComponent
  ) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
      this.currentLang = this._translateService.currentLang;
      // document.getElementsByClassName("page-count")[0].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRows;
      this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
      totalMessage: this._translateService.instant('LABEL.TOTAL')};
      // this.searchDirectory();

    })

    this.parent = currentParent;
  }

  ngOnInit(): void {
    this.messages = {
      emptyMessage: this._translateService.instant('LABEL.NO_DATA'),
      totalMessage: this._translateService.instant('LABEL.TOTAL')
    };
    
    this._coreTranslationService.translate(eng, vie);
    this.uploader = new FileUploader({
      isHTML5: true,
    });
    if(this.programId){
      this.getProofHaveNotFile(this.programId);
    }
  }

  validate(): Boolean {
    return this.organizationId && this.programId ? true : false;
  }

  async uploadAuto(i: number) {
    
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
        if (!this.validate()) {
          if (!this.programId) {
            Swal.fire({
              icon: "warning",
              title: this._translateService.instant(
                "MESSAGE.DIRECTORY.WARNING_REQUIRE_PROGRAM"
              ),
              confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
            });
          }
          if (!this.organizationId) {
            Swal.fire({
              icon: "warning",
              title: this._translateService.instant(
                "MESSAGE.DIRECTORY.WARNING_REQUIRE_ORGANIZATION"
              ),
              confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
            });
          }
          return;
        }
        Swal.fire(this._translateService.instant("MESSAGE.COMMON.UPLOADING_DATA"));
        Swal.showLoading();
        var index = 0;
        this.uploader.queue.forEach((element) => {
          if (index !== i) {
            index++;
            return;
          }
          this.service
            .collectExhibition(
              {
                content: {
                  programId: this.programId,
                  organizationId: this.organizationId,
                  forWhat: this.forWhat,
                },
              },
              element._file
            )
            .then((data) => {
              Swal.close();
              if (data.body.code === 0) {
                element.isUploaded = true;
                element.isSuccess = true;
                element.progress = 100;
                Swal.fire({
                  icon: "success",
                  title: this._translateService.instant(
                    "MESSAGE.AUTO_UPLOAD_EXHIBITION.SUCCESS_IMPORT"
                  ),
                  confirmButtonText:
                    this._translateService.instant("ACTION.ACCEPT"),
                }).finally(()=>{
                  this.getProofHaveNotFile(this.programId);
                });
              } else if (data.body.code == 7) {
                Swal.fire(
                  this._translateService.instant("MESSAGE.COMMON.NOT_EXCEL_FILE")
                );
              } else if (data.body.code == 8) {
                Swal.fire(
                  this._translateService.instant(
                    "MESSAGE.AUTO_UPLOAD_EXHIBITION.STANDARD_EXIST"
                  )
                );
              } else if (data.body.code == 9) {
                Swal.fire(
                  this._translateService.instant(
                    "MESSAGE.AUTO_UPLOAD_EXHIBITION.CRITERIA_EXIST"
                  )
                );
              } else if (data.body.code == 10) {
                Swal.fire(
                  this._translateService.instant(
                    "MESSAGE.AUTO_UPLOAD_EXHIBITION.PROOF_EXIST"
                  )
                );
              } else if (data.body.code == 11) {
                Swal.fire(
                  this._translateService.instant(
                    "MESSAGE.AUTO_UPLOAD_EXHIBITION.NOT_SAME_CODE"
                  )
                );
              } else if (data.body.code == 12) {
                Swal.fire(
                  this._translateService.instant(
                    "MESSAGE.AUTO_UPLOAD_EXHIBITION.NOT_FOUND_ORG"
                  )
                );
              } else if (data.body.code == 13) {
                Swal.fire("Có mã minh chứng không hợp lệ");
              } else if (data.body.code == 14) {
                Swal.fire({
                  icon: "error",
                  title: this._translateService.instant(
                    "MESSAGE.AUTO_UPLOAD_EXHIBITION.WRONG_FORMAT"
                  ),
                  confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
                });
              } else {
                element.isError = true;
                element.progress = 100;
              }
              if (element == this.uploader.queue[this.uploader.queue.length - 1]) {
                this.uploader.progress = 100;
                this.reloadFolder.emit();
              }
            });
          index = 0;
        });
      }
    });
  }

  proofListNotHaveFile;
  getProofHaveNotFile(programId) {
    if (programId == null) {
      return;
    }
    let params = {
      method: "GET",
      programId: programId,
      currentPage: this.currentPage,
      perPage: this.perPage,
    };
    this.service.getListProofHaveNotFile(params).then((data) => {
      let response = data;
      if (response.code === 0) {
        this.proofListNotHaveFile = response.content["items"];
        this.totalRows = response.content["total"];
      } else {
        Swal.fire({
          icon: "error",
          title: response.errorMessages,
        });
      }
    });
  }

  onClear() {
    this.proofListNotHaveFile = [];
    this.currentPage = 0;
    this.perPage = 10;
    this.totalRows = 0;
  }

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.getProofHaveNotFile(this.programId);
  }

  changePerpage() {
    this.getProofHaveNotFile(this.programId);
  }
  downloadMyFile() {
    const link = document.createElement("a");
    link.setAttribute("target", "_blank");
    if (this.forWhat == "criteria") {
      link.setAttribute(
        "href",
        "assets/template-excel/Danh muc minh chung(Tieu chuan).xlsx"
      );
      link.setAttribute("download", `Danh muc minh chung(Tieu chuan).xlsx`);
    } else {
      link.setAttribute(
        "href",
        "assets/template-excel/Danh muc minh chung (Tieu chi).xlsx"
      );
      link.setAttribute("download", `Danh muc minh chung (Tieu chi).xlsx`);
    }
    document.body.appendChild(link);
    link.click();
    link.remove();
  }
}
