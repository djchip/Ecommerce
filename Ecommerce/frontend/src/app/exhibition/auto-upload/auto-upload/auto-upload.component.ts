import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';
import { CoreTranslationService } from '@core/services/translation.service';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { AutoUploadService } from '../auto-upload.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-auto-upload',
  templateUrl: './auto-upload.component.html',
  styleUrls: ['./auto-upload.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class AutoUploadComponent implements OnInit {

  @Input() programId;

  constructor(
    private _coreTranslationService: CoreTranslationService,
    private modalService: NgbModal,
    public _translateService: TranslateService,
    public service: AutoUploadService) { }

  ngOnInit(): void {
    this._coreTranslationService.translate(eng, vie);
  }

  getCollectedExhibition(programId) {
    if(programId == null){
      return;
    }
    this.programId = programId;
    let params = {
      method: "GET", programId: programId
    };
    Swal.showLoading();
    this.service
      .getCollectedExhibition(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.nodesFilter = data.content;
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

  onClear(){
    this.programId = null;
    this.nodesFilter = [];
  }

  public selectedTreeList = [];

  onSelect(event) {
    this.selectedTreeList = Object.entries(event.treeModel.selectedLeafNodeIds)
      .filter(([key, value]) => {
        return (value === true);
      }).map((node) => node[0]);
  }

  // filter
  public optionsFilter = {
    useCheckbox: true
  };

  public nodesFilter = [];

  // modal Open Small
  openModalCollectExhibition(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  reloadFolder(){
    this.getCollectedExhibition(this.programId)
  }

  deleteCollection(){
    Swal.fire({
      title: this._translateService.instant('MESSAGE.AUTO_UPLOAD_EXHIBITION.CONFIRM_DELETE'),
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
        let params = {
          method: 'DELETE', content: this.selectedTreeList
        }
        this.service.deleteCollection(params).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.AUTO_UPLOAD_EXHIBITION.SUCCESS_DELETE'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.reloadFolder()
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
            title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          });
        });
      }
    });
  }

}
