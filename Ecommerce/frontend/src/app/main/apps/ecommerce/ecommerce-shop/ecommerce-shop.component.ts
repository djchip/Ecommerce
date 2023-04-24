import { Component, OnInit, ViewEncapsulation } from '@angular/core';

import { CoreSidebarService } from '@core/components/core-sidebar/core-sidebar.service';
import { EcommerceFakeData } from '@fake-db/ecommerce.data';
import { ProductService } from 'app/component/products/product.service';

import { EcommerceService } from 'app/main/apps/ecommerce/ecommerce.service';
@Component({
  selector: 'app-ecommerce-shop',
  templateUrl: './ecommerce-shop.component.html',
  styleUrls: ['./ecommerce-shop.component.scss'],
  encapsulation: ViewEncapsulation.None,
  host: { class: 'ecommerce-application' }
})
export class EcommerceShopComponent implements OnInit {
  // public
  public contentHeader: object;
  public shopSidebarToggle = false;
  public shopSidebarReset = false;
  public gridViewRef = true;
  public products;
  public wishlist;
  public cartList;
  public page = 1;
  public pageSize = 9;
  public searchText = '';

  /**
   *
   * @param {CoreSidebarService} _coreSidebarService
   * @param {EcommerceService} _ecommerceService
   */
  constructor(
    private _coreSidebarService: CoreSidebarService,
    private service: ProductService,
    private _ecommerceService: EcommerceService) { }

  toggleSidebar(name): void {
    this._coreSidebarService.getSidebarRegistry(name).toggleOpen();
  }

  listView() {
    this.gridViewRef = false;
  }

  gridView() {
    this.gridViewRef = true;
  }

  sortProduct(sortParam) {
    this._ecommerceService.sortProduct(sortParam);
  }

  getListProduct(){
    let params = {
      method: "GET",
    };
    this.service.getListProduct(params)
    .then((data) => {
      let response = data;
      if(response.code === 0){
        this.products = response.content;
      }
    })
  }

  ngOnInit(): void {
    // Subscribe to ProductList change
    
    this._ecommerceService.onProductListChange.subscribe(res => {
      this.products = res;
      this.products.isInWishlist = false;
    });

    // this.products = EcommerceFakeData.products;
    this.getListProduct();



    // Subscribe to Wishlist change
    this._ecommerceService.onWishlistChange.subscribe(res => (this.wishlist = res));

    // Subscribe to Cartlist change
    this._ecommerceService.onCartListChange.subscribe(res => (this.cartList = res));

    // update product is in Wishlist & is in CartList : Boolean
    this.products.forEach(product => {
      product.isInWishlist = this.wishlist.findIndex(p => p.productId === product.id) > -1;
      product.isInCart = this.cartList.findIndex(p => p.productId === product.id) > -1;
    });

    // content header
    this.contentHeader = {
      headerTitle: 'Shop',
      actionButton: true,
      breadcrumb: {
        type: '',
        links: [
          {
            name: 'Home',
            isLink: true,
            link: '/'
          },
          {
            name: 'eCommerce',
            isLink: true,
            link: '/'
          },
          {
            name: 'Shop',
            isLink: false
          }
        ]
      }
    };
  }
}
