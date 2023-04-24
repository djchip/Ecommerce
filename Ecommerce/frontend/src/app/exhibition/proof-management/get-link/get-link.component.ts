import { CoreTranslationService } from '@core/services/translation.service';
import { ChangeLanguageService } from './../../../services/change-language.service';
import { AutoUploadService } from './../../auto-upload/auto-upload.service';
import { Component, OnInit } from '@angular/core';
import {
  Router
} from "@angular/router";
import Swal from 'sweetalert2';
import { TranslateService } from '@ngx-translate/core';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';

@Component({
  selector: 'app-get-link',
  templateUrl: './get-link.component.html',
  styleUrls: ['./get-link.component.scss']
})
export class GetLinkComponent implements OnInit {
  public currentLang = this._translateService.currentLang;
  constructor(private route: Router, private service: AutoUploadService, private _coreTranslationService: CoreTranslationService,
    public _translateService: TranslateService, private _changeLanguageService: ChangeLanguageService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
      this.currentLang = this._translateService.currentLang;
    })
   }

  link
  ngOnInit(): void {
    this._coreTranslationService.translate(eng, vie);
    this.setLink();
    this.isLink = false;
  }

  contentType
  setLink() {
    this.checkLinkOrMedia(this.route.url);
    if(this.link == null) {
      return;
    }
    let params = {
      method: "GET", link: this.link, isLink: this.isLink
    };
    console.log("params : ___", this.link);
    Swal.showLoading();
    this.service
      .getLink(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.contentType = data.content;
          localStorage.setItem('content', this.contentType.content);
          localStorage.setItem('fileName', this.contentType.fileName);
          localStorage.setItem('type', this.contentType.type);
          localStorage.setItem('linkUrl', this.link);
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.AUTO_UPLOAD_EXHIBITION.DOWNLOAD_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            if (result.value) {
              window.self.close();
            }
          })
        } else {
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
      })
      
  }

  isLink=false;
  checkLinkOrMedia(url) {
    if(url.startsWith("/exhibition/proof/search?link=")) {
      this.link = url.replace("/exhibition/proof/search?link=","");
      this.isLink = true;
    } else {
      this.link = url.replace("/exhibition/proof/search?media=","");
    }
  }

}
