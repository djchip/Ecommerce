import { CoreCommonModule } from '@core/common.module';
import { FileUploadModule } from 'ng2-file-upload';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListCriteriaComponent } from './list-criteria/list-criteria.component';
import { Routes, RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CoreDirectivesModule } from '@core/directives/directives';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { AddCriteriaComponent } from './add-criteria/add-criteria.component';
import { EditCriteriaComponent } from './edit-criteria/edit-criteria.component';
import { DetaiCriteriaComponent } from './detail-criteria/detail-criteria.component';
import { TranslateModule } from '@ngx-translate/core';
import { ImportCriteriaComponent } from './import-criteria/import-criteria.component';
import { CardSnippetModule } from '@core/components/card-snippet/card-snippet.module';
import { ViewImportCriteriaComponent } from './view-import-criteria/view-import-criteria.component';

const routes: Routes = [

  {
    path: 'list',
    component: ListCriteriaComponent,
    data: { animation: 'feather' }
  },

];

@NgModule({
  declarations: [
    ListCriteriaComponent,
    AddCriteriaComponent,
    EditCriteriaComponent,
    DetaiCriteriaComponent,
    ImportCriteriaComponent,
    ViewImportCriteriaComponent
  ],
  imports: [
    NgSelectModule,
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    ReactiveFormsModule,
    NgxDatatableModule,
    CorePipesModule,
    CoreDirectivesModule,
    NgbModule,
    ContentHeaderModule,
    TranslateModule,
    CardSnippetModule,
    FileUploadModule,
    CoreCommonModule
  ],
  exports:[
    DetaiCriteriaComponent,
    ViewImportCriteriaComponent
  ]
})
export class CriteriaModule { }
