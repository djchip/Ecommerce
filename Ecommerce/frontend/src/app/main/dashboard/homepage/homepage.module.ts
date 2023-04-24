import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HomepageComponent } from './homepage/homepage.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule } from '@ngx-translate/core';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { NgApexchartsModule } from 'ng-apexcharts';
import { CardSnippetModule } from '@core/components/card-snippet/card-snippet.module';
import { Ng2FlatpickrModule } from 'ng2-flatpickr';
import { CoreCommonModule } from '@core/common.module';
import { CardAnalyticsComponent } from 'app/main/ui/card/card-analytics/card-analytics.component';
import { CardAnalyticsService } from 'app/main/ui/card/card-analytics/card-analytics.service';
const routes: Routes = [
  {
    path: 'list',
    component: HomepageComponent,
  },
  {
    path: 'card/analytics',
    component: CardAnalyticsComponent,
    resolve: {
      css: CardAnalyticsService
    },
    data: { animation: 'analytics' }
  }
];

@NgModule({
  declarations: [
    HomepageComponent,
  ],
  imports: [
    RouterModule.forChild(routes),
    CommonModule,
    NgbModule,
    TranslateModule,
    ContentHeaderModule,
    NgApexchartsModule,
    CardSnippetModule,
    Ng2FlatpickrModule,
    CoreCommonModule,
  ],
  providers: [CardAnalyticsService]
})
export class HomepageModule { }
