import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CoreTranslationService } from '@core/services/translation.service';
import { TranslateService } from '@ngx-translate/core';
import { CategoryService } from 'app/component/category/category.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import Swal from 'sweetalert2';
import { ProductService } from '../product.service';

@Component({
  selector: 'app-detail-product',
  templateUrl: './detail-product.component.html',
  styleUrls: ['./detail-product.component.scss']
})
export class DetailProductComponent implements OnInit {

  data;
  @Input() productId;
  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");

  constructor(
    private _changeLanguageService: ChangeLanguageService,
    private service: ProductService,
    public _translateService: TranslateService,
  ) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
      
    });
  }

  ngOnInit(): void {
    this.getCategoryDetail();
  }

  async getCategoryDetail() {
    if (this.productId) {
      let params = {
        method: "GET",
      };
      Swal.showLoading();
      await this.service
        .detailProduct(params, this.productId)
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

}
