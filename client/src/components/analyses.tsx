import type { Analysis } from '@/lib/types'
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion'
import MarkdownRenderer from '@/components/ui/markdown-renderer'
import { Skeleton } from './ui/skeleton'
import { useGlobalState } from '@/hooks/use-global-state'
import { CircleMinus, Refresh } from './icons/tabler'
import { toast } from 'sonner'
import { cn } from '@/lib/utils'
import { handleSearch } from '@/lib/handleSearch'

function Summary({ summary }: { summary: string }) {
  return (
    <>
      <strong className="text-2xl">Summary:</strong>
      <div className="mb-2 w-full rounded-md border p-4 text-left">
        {summary}
      </div>
    </>
  )
}

function RelatedDocs({
  related_docs,
}: {
  related_docs: Analysis['content'][number]['related_docs']
}) {
  return (
    <>
      <strong className="text-2xl">Related Docs:</strong>
      <div className="mb-2 w-full rounded-md border p-4 text-left">
        <ul className="list-disc">
          {related_docs.length > 0 ? (
            related_docs.map((doc) => {
              return (
                <li key={doc} className="hover:underline">
                  <a href={doc} target="_blank">
                    {doc}
                  </a>
                </li>
              )
            })
          ) : (
            <li>None</li>
          )}
        </ul>
      </div>
    </>
  )
}

function DetailedAnalysis({
  detailed_analysis,
}: {
  detailed_analysis: string
}) {
  return (
    <>
      <strong className="text-2xl">Detailed Analysis:</strong>
      <div className="w-full rounded-md border p-4 text-left">
        <MarkdownRenderer>{detailed_analysis}</MarkdownRenderer>
      </div>
    </>
  )
}

async function deleteAnalysis(login: string, analysisId: string) {
  const res = await fetch(
    `${import.meta.env.VITE_USERS_URL}/users/${login}/analysis/${analysisId}`,
    {
      method: 'DELETE',
    },
  )
  if (!res.ok) {
    console.error('Failed to delete analysis:', res.text())
    toast.error('Failed to remove analysis')
    return
  }
  toast.info('Analysis removed')
}

function Analysis(analysis: Analysis) {
  const createdAt = analysis.created_at.toLocaleString('de-DE', {
    timeZone: 'Europe/Berlin',
  })
  const login = useGlobalState((state) => state.login)
  const addAnalysis = useGlobalState((state) => state.addAnalysis)
  return (
    <AccordionItem value={analysis.id}>
      <AccordionTrigger
        className={cn(
          'text-center border m-1 p-2',
          analysis.id === 'unknown' ? 'bg-amber-700 hover:bg-amber-700/80' : '',
        )}
      >
        <div className="flex flex-1 justify-between items-center min-h-[24px]">
          <div>
            <strong>Repository: </strong>
            {analysis.repository}
          </div>
          <div className="flex gap-2 items-center">
            <span>{createdAt}</span>
            {analysis.id !== 'unknown' && (
              <div
                className="cursor-pointer text-red-500 hover:text-red-700"
                onClick={(e) => {
                  if (!login) {
                    toast.error('You must be logged in to delete analyses')
                    return
                  }
                  e.stopPropagation()
                  void deleteAnalysis(login, analysis.id)
                  useGlobalState.setState((state) => ({
                    analyses: state.analyses.filter(
                      (a) => a.id !== analysis.id,
                    ),
                  }))
                }}
              >
                <CircleMinus />
              </div>
            )}
            <div
              className="cursor-pointer text-blue-500 hover:text-blue-700"
              onClick={(e) => {
                e.stopPropagation()
                // eslint-disable-next-line @typescript-eslint/no-empty-function
                handleSearch(analysis.repository, () => {}, addAnalysis)
              }}
            >
              <Refresh />
            </div>
          </div>
        </div>
      </AccordionTrigger>
      <AccordionContent className="flex flex-col gap-4 text-balance">
        <Accordion type="single" collapsible>
          {analysis.content.map((content) => (
            <AccordionItem value={content.filename} key={content.filename}>
              <AccordionTrigger className="mx-8 my-1 px-2 border">
                {content.filename}
              </AccordionTrigger>
              <AccordionContent className="flex flex-col gap-4 mx-8 items-center">
                <Summary summary={content.summary} />
                <DetailedAnalysis
                  detailed_analysis={content.detailed_analysis}
                />
                <RelatedDocs related_docs={content.related_docs} />
              </AccordionContent>
            </AccordionItem>
          ))}
        </Accordion>
      </AccordionContent>
    </AccordionItem>
  )
}

export default function Analyses() {
  const analyses = useGlobalState((state) => state.analyses)
  const loading = useGlobalState((state) => state.loading)
  return (
    <div className="mt-8">
      <h2 className="text-4xl m-2">Analyses:</h2>
      <Accordion type="single" collapsible className="w-full">
        {loading ? (
          <Skeleton className="h-20 w-full" />
        ) : analyses.length === 0 ? (
          <div className="text-center text-gray-500">
            No analyses available yet.
          </div>
        ) : (
          analyses.map((analysis) => (
            <Analysis
              key={analysis.id + analysis.created_at.toISOString()}
              {...analysis}
            />
          ))
        )}
      </Accordion>
    </div>
  )
}
