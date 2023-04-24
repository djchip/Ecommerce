import { CoreTouchspinModule } from '@core/components/core-touchspin/core-touchspin.module';
import { CoreCardModule } from './../../../@core/components/core-card/core-card.module';
import { TranslateModule } from '@ngx-translate/core';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { FormsModule,ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { ContentHeaderModule } from './../../layout/components/content-header/content-header.module';
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UploadDatabaseComponent } from './upload-database/upload-database.component';
import { DatabaseComponent } from './database/database.component';
import { DatabaseObjectComponent } from './database-object/database-object.component';
import { NgxMaskModule } from 'ngx-mask';
import localeVi from '@angular/common/locales/vi';
import { registerLocaleData } from '@angular/common';
import { CardSnippetModule } from '@core/components/card-snippet/card-snippet.module';
import { FileUploadModule } from 'ng2-file-upload';
registerLocaleData(localeVi,'vi')

const routes: Routes = [
  {
    path: "list",
    component: DatabaseComponent,
    data: {animation: 'feather'}
  }
]

@NgModule({
  declarations: [
    UploadDatabaseComponent,
    DatabaseComponent,
    DatabaseObjectComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    ContentHeaderModule,
    NgSelectModule,
    FormsModule,
    ReactiveFormsModule,
    CorePipesModule,
    NgbModule,
    CoreDirectivesModule,
    NgxDatatableModule,
    TranslateModule,
    CoreCardModule,
    NgxMaskModule.forRoot(),
    CoreTouchspinModule,
    CardSnippetModule,
    FileUploadModule
  ]
})
export class DatabaseModule { }
