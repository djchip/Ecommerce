import { UnitManagementService } from "./../unit-managerment.service";
import { Component, Input, OnInit, ViewEncapsulation, ChangeDetectorRef } from '@angular/core';
import { CoreTranslationService } from '@core/services/translation.service';
import { TranslateService } from '@ngx-translate/core';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { locale as eng } from "assets/languages/en";
import { locale as vie } from "assets/languages/vn";
import Swal from 'sweetalert2';


@Component({
  selector: 'app-view-import-unit',
  templateUrl: './view-import-unit.component.html',
  styleUrls: ['./view-import-unit.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ViewImportUnitComponent implements OnInit {
  @Input() obj;
  public currentLang = this._translateService.currentLang;
  constructor (    
    private _coreTranslationService: CoreTranslationService,
    private _changeLanguageService: ChangeLanguageService,
    public _translateService: TranslateService,
    private service: UnitManagementService) {
      this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
      })
     }

  ngOnInit(): void {
    this._coreTranslationService.translate(eng, vie);
    this.formatObjDir();
  }

  async formatObjDir() {
    Swal.showLoading();
    this.obj.forEach(element => {
      element.createdDate = null;
      element.updatedDate = null;
      if(element?.userInfos){
        element.userInfos.map(u => { u.unit.createdDate=null; u.unit.updatedDate = null; })
      }
    });
    let params = {
      method: "POST",
      content: this.obj,
    };
    await this.service
      .formatObj(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.obj = response.content;
          Swal.close();
        }
        else {
          Swal.close();
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
