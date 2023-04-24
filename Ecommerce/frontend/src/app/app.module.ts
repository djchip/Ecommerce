import { TreeviewModule } from 'ngx-treeview';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';

// import { ChatbotRasaModule } from 'angular-chat-widget-rasa';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { HttpClientInMemoryWebApiModule } from 'angular-in-memory-web-api';
import { FakeDbService } from '@fake-db/fake-db.service';
import { QuillModule } from 'ngx-quill';
import 'hammerjs';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ToastrModule } from 'ngx-toastr';
import { TranslateModule } from '@ngx-translate/core';
import { ContextMenuModule } from '@ctrl/ngx-rightclick';

import { CoreModule } from '@core/core.module';
import { CoreCommonModule } from '@core/common.module';
import { CoreSidebarModule, CoreThemeCustomizerModule } from '@core/components';
import { CardSnippetModule } from '@core/components/card-snippet/card-snippet.module';

import { coreConfig } from 'app/app-config';
import { AuthGuard } from 'app/auth/helpers/auth.guards';
import { fakeBackendProvider } from 'app/auth/helpers'; // used to create fake backend
import { JwtInterceptor, ErrorInterceptor } from 'app/auth/helpers';
import { AppComponent } from 'app/app.component';
// import { LayoutModule } from 'app/layout/layout.module';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';

import { ContextMenuComponent } from 'app/main/extensions/context-menu/context-menu.component';
import { AnimatedCustomContextMenuComponent } from './main/extensions/context-menu/custom-context-menu/animated-custom-context-menu/animated-custom-context-menu.component';
import { BasicCustomContextMenuComponent } from './main/extensions/context-menu/custom-context-menu/basic-custom-context-menu/basic-custom-context-menu.component';
import { SubMenuCustomContextMenuComponent } from './main/extensions/context-menu/custom-context-menu/sub-menu-custom-context-menu/sub-menu-custom-context-menu.component';
import { HttpUtilService } from './services/http-util.service';
import { TokenStorage } from './services/token-storage.service';
import { AdministratorsModule } from './administrators/administrators.module';
import { NgSelectModule } from '@ng-select/ng-select';
import { LayoutModule } from './main/charts-and-maps/layout.module';
import { ExhibitionModule } from './exhibition/exhibition.module';
import { SelfAssessmentModule } from './self-assessment/self-assessment.module';
import { AcademyDatabaseModule } from './academy-database/academy-database.module';
import { ListProductComponent } from './component/products/list-product/list-product.component';


const appRoutes: Routes = [
  {
    path: '',
    redirectTo: 'ecommerce',
    pathMatch: 'full'
    
  },
  {
    path: 'category',
    loadChildren: () => import('./component/category/category.module').then(m => m.CategoryModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'products',
    loadChildren: () => import('./component/products/product.module').then(m => m.ProductModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'ecommerce',
    loadChildren: () => import('./main/apps/ecommerce/ecommerce.module').then(m => m.EcommerceModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./main/dashboard/dashboard.module').then(m => m.DashboardModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'apps',
    loadChildren: () => import('./main/apps/apps.module').then(m => m.AppsModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'pages',
    loadChildren: () => import('./main/pages/pages.module').then(m => m.PagesModule)
  },
  {
    path: 'ui',
    loadChildren: () => import('./main/ui/ui.module').then(m => m.UIModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'components',
    loadChildren: () => import('./main/components/components.module').then(m => m.ComponentsModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'extensions',
    loadChildren: () => import('./main/extensions/extensions.module').then(m => m.ExtensionsModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'forms',
    loadChildren: () => import('./main/forms/forms.module').then(m => m.FormsModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'tables',
    loadChildren: () => import('./main/tables/tables.module').then(m => m.TablesModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'charts-and-maps',
    loadChildren: () => import('./main/charts-and-maps/charts-and-maps.module').then(m => m.ChartsAndMapsModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'admin',
    loadChildren: () => import('./administrators/administrators.module').then(m => m.AdministratorsModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'exhibition',
    loadChildren: () => import('./exhibition/exhibition.module').then(m => m.ExhibitionModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'database',
    loadChildren: () => import('./academy-database/academy-database.module').then(m => m.AcademyDatabaseModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'assessment',
    loadChildren: () => import('./self-assessment/self-assessment.module').then(m => m.SelfAssessmentModule),
    canActivate: [AuthGuard]
  }
];

@NgModule({
  declarations: [
    AppComponent,
    ContextMenuComponent,
    BasicCustomContextMenuComponent,
    AnimatedCustomContextMenuComponent,
    SubMenuCustomContextMenuComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    NgSelectModule,
    HttpClientModule,
    HttpClientInMemoryWebApiModule.forRoot(FakeDbService, {
      delay: 0,
      passThruUnknownUrl: true
    }),
    RouterModule.forRoot(appRoutes, {
      scrollPositionRestoration: 'enabled', // Add options right here
      relativeLinkResolution: 'legacy'
    }),
    NgbModule,
    ToastrModule.forRoot(),
    TranslateModule.forRoot(),
    ContextMenuModule,
    CoreModule.forRoot(coreConfig),
    CoreCommonModule,
    CoreSidebarModule,
    CoreThemeCustomizerModule,
    CardSnippetModule,
    LayoutModule,
    ContentHeaderModule,
    AdministratorsModule,
    QuillModule.forRoot(),
    NgxDatatableModule,
    ExhibitionModule,
    SelfAssessmentModule,
    AcademyDatabaseModule
  ],

  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    HttpUtilService,
    TokenStorage,
    // ! IMPORTANT: Provider used to create fake backend, comment while using real API
    fakeBackendProvider
  ],
  entryComponents: [BasicCustomContextMenuComponent, AnimatedCustomContextMenuComponent],
  bootstrap: [AppComponent]
})
export class AppModule { }
