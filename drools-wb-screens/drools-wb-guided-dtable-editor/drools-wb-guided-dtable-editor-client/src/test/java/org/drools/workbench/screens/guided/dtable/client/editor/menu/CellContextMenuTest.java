/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.editor.menu;

import java.util.ArrayList;
import java.util.HashSet;

import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.screens.guided.dtable.client.editor.clipboard.Clipboard;
import org.drools.workbench.screens.guided.dtable.client.editor.clipboard.impl.DefaultClipboard;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectionsChangedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CellContextMenuTest {

    private CellContextMenu menu;
    private GuidedDecisionTable52 model;
    private GuidedDecisionTableUiModel uiModel;
    private Clipboard clipboard;

    @Mock
    private CellContextMenuView view;

    @Mock
    private GuidedDecisionTableView.Presenter dtPresenter;
    private GuidedDecisionTablePresenter.Access access = new GuidedDecisionTablePresenter.Access();

    @Mock
    private GuidedDecisionTableView dtPresenterView;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        model = new GuidedDecisionTable52();
        uiModel = new GuidedDecisionTableUiModel( mock( ModelSynchronizer.class ) );
        clipboard = new DefaultClipboard();

        when( dtPresenter.getView() ).thenReturn( dtPresenterView );
        when( dtPresenter.getModel() ).thenReturn( model );
        when( dtPresenter.getAccess() ).thenReturn( access );
        when( dtPresenterView.getModel() ).thenReturn( uiModel );

        uiModel.appendColumn( new BaseGridColumn<String>( mock( GridColumn.HeaderMetaData.class ),
                                                          mock( GridColumnRenderer.class ),
                                                          100 ) );
        uiModel.appendColumn( new BaseGridColumn<String>( mock( GridColumn.HeaderMetaData.class ),
                                                          mock( GridColumnRenderer.class ),
                                                          100 ) );
        uiModel.appendColumn( new BaseGridColumn<String>( mock( GridColumn.HeaderMetaData.class ),
                                                          mock( GridColumnRenderer.class ),
                                                          100 ) );
        uiModel.appendRow( new BaseGridRow() );

        menu = new CellContextMenu( view,
                                    clipboard );
        menu.setup();
    }

    @Test
    public void testOnDecisionTableSelectedEventWithNoSelections() {
        menu.onDecisionTableSelectedEvent( new DecisionTableSelectedEvent( dtPresenter ) );

        verify( view,
                times( 1 ) ).enableCutMenuItem( eq( false ) );
        verify( view,
                times( 1 ) ).enableCopyMenuItem( eq( false ) );
        verify( view,
                times( 1 ) ).enablePasteMenuItem( eq( false ) );
        verify( view,
                times( 1 ) ).enableDeleteCellMenuItem( eq( false ) );
    }

    @Test
    public void testOnDecisionTableSelectedEventWithSelections() {
        model.getMetadataCols().add( new MetadataCol52() );
        model.getData().add( new ArrayList<DTCellValue52>() {{
            add( new DTCellValue52( 1 ) );
            add( new DTCellValue52( "descr" ) );
            add( new DTCellValue52( "md" ) );
        }} );

        uiModel.selectCell( 0,
                            2 );

        menu.onDecisionTableSelectedEvent( new DecisionTableSelectedEvent( dtPresenter ) );

        verify( view,
                times( 1 ) ).enableCutMenuItem( eq( true ) );
        verify( view,
                times( 1 ) ).enableCopyMenuItem( eq( true ) );
        verify( view,
                times( 1 ) ).enablePasteMenuItem( eq( false ) );
        verify( view,
                times( 1 ) ).enableDeleteCellMenuItem( eq( true ) );
    }

    @Test
    public void testOnDecisionTableSelectedEventWithSelectionsWithClipboardPopulated() {
        model.getMetadataCols().add( new MetadataCol52() );
        model.getData().add( new ArrayList<DTCellValue52>() {{
            add( new DTCellValue52( 1 ) );
            add( new DTCellValue52( "descr" ) );
            add( new DTCellValue52( "md" ) );
        }} );

        uiModel.selectCell( 0,
                            2 );
        clipboard.setData( new HashSet<Clipboard.ClipboardData>() {{
            add( new DefaultClipboard.ClipboardDataImpl( 0,
                                                         2,
                                                         model.getData().get( 0 ).get( 2 ) ) );
        }} );

        menu.onDecisionTableSelectedEvent( new DecisionTableSelectedEvent( dtPresenter ) );

        verify( view,
                times( 1 ) ).enableCutMenuItem( eq( true ) );
        verify( view,
                times( 1 ) ).enableCopyMenuItem( eq( true ) );
        verify( view,
                times( 1 ) ).enablePasteMenuItem( eq( true ) );
        verify( view,
                times( 1 ) ).enableDeleteCellMenuItem( eq( true ) );
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithNoSelections() {
        menu.onDecisionTableSelectionsChangedEvent( new DecisionTableSelectionsChangedEvent( dtPresenter ) );

        verify( view,
                times( 1 ) ).enableCutMenuItem( eq( false ) );
        verify( view,
                times( 1 ) ).enableCopyMenuItem( eq( false ) );
        verify( view,
                times( 1 ) ).enablePasteMenuItem( eq( false ) );
        verify( view,
                times( 1 ) ).enableDeleteCellMenuItem( eq( false ) );
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithSelections() {
        model.getMetadataCols().add( new MetadataCol52() );
        model.getData().add( new ArrayList<DTCellValue52>() {{
            add( new DTCellValue52( 1 ) );
            add( new DTCellValue52( "descr" ) );
            add( new DTCellValue52( "md" ) );
        }} );

        uiModel.selectCell( 0,
                            2 );

        menu.onDecisionTableSelectionsChangedEvent( new DecisionTableSelectionsChangedEvent( dtPresenter ) );

        verify( view,
                times( 1 ) ).enableCutMenuItem( eq( true ) );
        verify( view,
                times( 1 ) ).enableCopyMenuItem( eq( true ) );
        verify( view,
                times( 1 ) ).enablePasteMenuItem( eq( false ) );
        verify( view,
                times( 1 ) ).enableDeleteCellMenuItem( eq( true ) );
    }

    @Test
    public void testOnDecisionTableSelectionsChangedEventWithSelectionsWithClipboardPopulated() {
        model.getMetadataCols().add( new MetadataCol52() );
        model.getData().add( new ArrayList<DTCellValue52>() {{
            add( new DTCellValue52( 1 ) );
            add( new DTCellValue52( "descr" ) );
            add( new DTCellValue52( "md" ) );
        }} );

        uiModel.selectCell( 0,
                            2 );
        clipboard.setData( new HashSet<Clipboard.ClipboardData>() {{
            add( new DefaultClipboard.ClipboardDataImpl( 0,
                                                         2,
                                                         model.getData().get( 0 ).get( 2 ) ) );
        }} );

        menu.onDecisionTableSelectionsChangedEvent( new DecisionTableSelectionsChangedEvent( dtPresenter ) );

        verify( view,
                times( 1 ) ).enableCutMenuItem( eq( true ) );
        verify( view,
                times( 1 ) ).enableCopyMenuItem( eq( true ) );
        verify( view,
                times( 1 ) ).enablePasteMenuItem( eq( true ) );
        verify( view,
                times( 1 ) ).enableDeleteCellMenuItem( eq( true ) );
    }

    @Test
    public void testOnDecisionTableSelectedEventReadOnly() {
        dtPresenter.getAccess().setReadOnly( true );
        menu.onDecisionTableSelectedEvent( new DecisionTableSelectedEvent( dtPresenter ) );

        verify( view,
                times( 1 ) ).enableCutMenuItem( eq( false ) );
        verify( view,
                times( 1 ) ).enableCopyMenuItem( eq( false ) );
        verify( view,
                times( 1 ) ).enablePasteMenuItem( eq( false ) );
        verify( view,
                times( 1 ) ).enableDeleteCellMenuItem( eq( false ) );
    }

}
