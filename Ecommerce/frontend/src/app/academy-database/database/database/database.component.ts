import { CoreTranslationService } from '@core/services/translation.service';
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';

@Component({
  selector: 'app-database',
  templateUrl: './database.component.html',
  styleUrls: ['./database.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class DatabaseComponent implements OnInit {
  public contentHeader: object;

  constructor(private _coreTranslationService: CoreTranslationService) { }

  ngOnInit(): void {
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.LIST_DATABASE',
      actionButton: true,
      breadcrumb: {
        type: '',
        links: [
          {
            name: 'CONTENT_HEADER.MAIN_PAGE',
            isLink: true,
            link: '/dashboard'
          },
          {
            name: 'MENU.DATABASE',
            isLink: false,
            link: '/'
          },
          {
            name: 'CONTENT_HEADER.DATABASE_MANAGEMENT',
            isLink: false
          }
        ]
      }
    };
    this._coreTranslationService.translate(eng, vie);
  }

}
