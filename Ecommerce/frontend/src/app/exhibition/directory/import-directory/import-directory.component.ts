import { ProgramService } from './../../programs-management/programs.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { CoreTranslationService } from './../../../../@core/services/translation.service';
import { DirectoryService } from "./../directory.service";
import { FormGroup, FormBuilder, Validators } from "@angular/forms";
import { FileUploader } from "ng2-file-upload";
import { Component, OnInit, ChangeDetectionStrategy, ViewEncapsulation, Output, EventEmitter } from "@angular/core";
import Swal from "sweetalert2";
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: "app-import-directory",
  templateUrl: "./import-directory.html",
  styleUrls: ["./import-directory.scss"],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ImportDirectoryComponent implements OnInit {
  public uploader: FileUploader;
  public importFG: FormGroup;
  public currentLang = this._translateService.currentLang;
  ImportSubmitted = false;
  disableCat = true;

  public listCat = [];


  @Output() reloadFolder = new EventEmitter<string>();

  constructor(
    private programService: DirectoryService,
    private formBuilder: FormBuilder,
    public _translateService: TranslateService,
    private _coreTranslationService: CoreTranslationService,
    private service: DirectoryService,
    private _changeLanguageService: ChangeLanguageService,
    private _serviceProgram: ProgramService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    })
  }

  ngOnInit(): void {
    this.uploader = new FileUploader({
      isHTML5: true,
    });
    this.autoFormG();
    // this.getLisCategories();
    this.getListOrganization();
    this._coreTranslationService.translate(eng, vie);
  }

  autoFormG() {
    this.importFG = this.formBuilder.group({
      organizationId: [null, [Validators.required]],
      categoryId: [null, Validators.required],
    });
  }
  resetCat() {
    this.disableCat = true;
  }
  listOrganization = [];
  getListOrganization() {
    let params = {
      method: "GET",
    };
    this._serviceProgram
      .getListOrganization(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listOrganization = data.content;
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

  get ImportFG() {
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
      if (result.value) {
        if (!this.validate()) {
          return;
        };
        Swal.fire(this._translateService.instant('MESSAGE.COMMON.UPLOADING_DATA'))
        Swal.showLoading();
        var index = 0;
        this.uploader.queue.forEach(element => {
          if (index !== i) {
            index++;
            return;
          }
          this.programService.import(element._file, {
            content: {
              organizationId: this.importFG.value.organizationId,
              categoryId: this.importFG.value.categoryId
            }
          }).then((data) => {
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
            } else if (data.body.code == 7) {
              Swal.fire(this._translateService.instant('MESSAGE.COMMON.NOT_EXCEL_FILE'));
            } else if (data.body.code == 3) {
              Swal.fire(this._translateService.instant('MESSAGE.DIRECTORY.ALREADY_EXITS'));
            }
            else if (data.body.code == 5) {
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
          }).catch(e => {
            Swal.fire(this._translateService.instant('MESSAGE.COMMON.SWW'));
          })
          index = 0;
        })
        this.ImportSubmitted = false;
      }
    });
  }

  validate(): Boolean {
    this.ImportSubmitted = true;
    return this.importFG.invalid ? false : true;
  }

  downloadMyFile() {
    const link = document.createElement('a');
    link.setAttribute('target', '_blank');
    link.setAttribute('href', 'assets/template-excel/Mau tieu chuan.xlsx');
    link.setAttribute('download', `Mau tieu chuan.xlsx`);
    document.body.appendChild(link);
    link.click();
    link.remove();
  }

  clear() {
    this.uploader.clearQueue();
  }
  // getLisCategories() {
  //   let params = {
  //     method: "GET",
  //   };
  //   Swal.showLoading();
  //   this.service
  //     .getLisCategories(params)
  //     .then((data) => {
  //       Swal.close();
  //       let response = data;
  //       if (response.code === 0) {
  //         this.listCat = data.content;
  //       } else {
  //         Swal.fire({
  //           icon: "error",
  //           title: response.errorMessages,
  //         });
  //       }
  //     })
  //     .catch((error) => {
  //       Swal.close();
  //       Swal.fire({
  //         icon: "error",
  //         title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
  //         confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
  //       });
  //     });
  // }


  onChangee() {
    this.disableCat = false;
 
    if (this.importFG.value.organizationId != null) {
      let params = {
        method: "GET",
        OrgId: this.importFG.value.organizationId,
      };
      Swal.showLoading();
      this.service
        .getLisCategorie(params)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.listCat = data.content;
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
}
